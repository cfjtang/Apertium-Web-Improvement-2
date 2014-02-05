#!/usr/bin/env python3

import argparse, requests, json, logging, sys, re, subprocess, urllib.request
import xml.etree.ElementTree as etree
try:
    from lexccounter import countStems
except ImportError:
    logging.error('Unable to import LEXC Stem Counter, proceeding without it.')

baseURL = 'http://wiki.apertium.org/w/api.php'
svnURL = 'https://svn.code.sf.net/p/apertium/svn/'

def getCounts(uri, dixFormat):
    try:
        dixString = str((urllib.request.urlopen(uri)).read(), 'utf-8')
        if 'dix' in dixFormat:
            dixTree = etree.fromstring(dixString)

        if dixFormat == 'monodix':
            return {'stems': len(dixTree.findall("section/*[@lm]")), 'paradigms': len(dixTree.find('pardefs').findall("pardef")) }
        elif dixFormat == 'bidix':
            return {'stems': len(dixTree.findall("*[@id='main']/e//l"))}
        elif dixFormat == 'lexc':
            logger = logging.getLogger("countStems")
            logger.setLevel(logging.ERROR)
            return {'stems': countStems(dixString)}
        else:
            raise ValueError('Invalid format: %s' % dixFormat)
    except Exception as e:
        logger.error('Unable to parse counts from %s: %s' % (uri, str(e)))
        return {}

def getDixNames(uri, dixFormat):
    try:
        svnData = str(subprocess.check_output('svn list --xml %s' % uri, stderr=subprocess.STDOUT, shell=True), 'utf-8')
        return re.findall(r'<name>([^\.]+\.[^\.]+\.(?:meta)?%s)</name>' % dixFormat, svnData, re.DOTALL)
    except subprocess.CalledProcessError:
        logging.error('No dix found for %s' % uri)
        return []

def getRevision(uri):
    try:
        svnData = str(subprocess.check_output('svn info -r HEAD %s --xml' % uri, stderr=subprocess.STDOUT, shell=True), 'utf-8')
        return re.findall(r'revision="([0-9]+)"', svnData, re.DOTALL)[0]
    except:
        return None
        
def getPage(pageTitle):
    payload = {'action': 'query', 'format': 'json', 'titles': pageTitle, 'prop': 'revisions', 'rvprop': 'content'}
    viewResult = s.get(baseURL, params=payload)
    jsonResult = json.loads(viewResult.text)
    
    if not 'missing' in list(jsonResult['query']['pages'].values())[0]:
        return list(jsonResult['query']['pages'].values())[0]['revisions'][0]['*']
        
def editPage(pageTitle, pageContents, editToken):
    payload = {'action': 'edit', 'format': 'json', 'title': pageTitle, 'text': pageContents, 'bot': 'True', 'contentmodel': 'wikitext', 'token': editToken}
    editResult = s.post(baseURL, params=payload)
    jsonResult = json.loads(editResult.text)
    
    return jsonResult

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Apertium Wiki Bot")
    parser.add_argument('loginName', help="bot login name")
    parser.add_argument('password', help="bot password")
    parser.add_argument('pairs', nargs='+', help="Apertium language pairs in the format e.g. incubator/apertium-bg-ru")

    args = parser.parse_args()
    logging.basicConfig(level=logging.INFO)
    logger = logging.getLogger(__name__)
    s = requests.Session()

    try:
        payload = {'action': 'login', 'format': 'json', 'lgname': args.loginName, 'lgpassword': args.password}
        authResult = s.post(baseURL, params=payload)
        authToken = json.loads(authResult.text)['login']['token']
        logging.debug('Auth token: %s' % authToken)

        payload = {'action': 'login', 'format': 'json', 'lgname': args.loginName, 'lgpassword': args.password, 'lgtoken': authToken}
        authResult = s.post(baseURL, params=payload)
        if not json.loads(authResult.text)['login']['result'] == 'Success':
            logging.critical('Failed to login as %s: %s' % (args.loginName, json.loads(authResult.text)['login']['result']))
            sys.exit(-1)
        else:
            logging.info('Login as %s succeeded' % args.loginName)
    except Exception as e:
        logging.critical('Failed to login: %s' % e)
        sys.exit(-1)
    try:
        payload = {'action': 'query', 'format': 'json', 'prop': 'info', 'intoken': 'move', 'titles':'Main Page'}
        moveTokenResult = s.get(baseURL, params=payload)
        moveToken = json.loads(moveTokenResult.text)['query']['pages']['1']['movetoken']
        logging.debug('Move token: %s' % moveToken)
    except Exception as e:
        logging.error('Failed to obtain move token: %s' % e)

    try:
        payload = {'action': 'query', 'format': 'json', 'prop': 'info|revisions', 'intoken': 'edit', 'titles':'Main Page'}
        editTokenResult = s.get(baseURL, params=payload)
        editToken = json.loads(editTokenResult.text)['query']['pages']['1']['edittoken']
        logging.debug('Edit token: %s' % editToken)
    except Exception as e:
        logging.error('Failed to obtain edit token: %s' % e)

    SVNRevision = getRevision(svnURL)
    logging.info('On SVN revision %s' % SVNRevision)

    for pair in args.pairs:
        try:
            langs = pair.split('/')[1].split('-')[1:]
            pageTitle = 'Apertium-' + '-'.join(langs) + '/stats'
        except:
            logging.error('Failed to parse language module name: %s' % pair)
            break

        if len(langs) == 2:
            dixNames = getDixNames(svnURL + pair, 'dix')
            dixCounts = {}
            for dixName in dixNames:
                dixPair = dixName.split('.')[1].split('-')
                counts = getCounts('{}{}/{}'.format(svnURL, pair, dixName), 'bidix' if set(langs) == set(dixPair) else 'monodix')
                for countType, count in counts.items():
                    dixCounts['-'.join(dixPair + [countType])] = count
            logging.debug('Acquired dictionary counts %s' % dixCounts)

            pageContents = getPage(pageTitle)
            if pageContents:
                matchAttempts = re.finditer(r'^\*[^<]+(<section begin=([^/]+)/>.*?$)', pageContents, re.MULTILINE)
                replacements = {}
                for matchAttempt in matchAttempts:
                    countName = matchAttempt.group(2).strip()
                    if countName in dixCounts:
                        replacement = "<section begin={0} />{1:,d}<section end={0} /> as of r{2} ~ ~~~~".format(countName, dixCounts[countName], SVNRevision)
                        replacements[(matchAttempt.group(1))] = replacement
                for old, new in replacements.items():
                    pageContents = pageContents.replace(old, new)

                editResult = editPage(pageTitle, pageContents, editToken)
                if editResult['edit']['result'] == 'Success':
                    logging.info('Update of page %s succeeded' % pageTitle)
                else:
                    logging.error('Update of page %s failed: %s' % (pageTitle, editResult))
            else:
                pageContents = '==Over-all stats=='
                for dixName, dixCount in dixCounts.items():
                    pageContents += "\n*'''{0}''': <section begin={0} />{1:,d}<section end={0} /> as of r{2} ~ ~~~~".format(dixName, dixCount, SVNRevision)

                editResult = editPage(pageTitle, pageContents, editToken)
                if editResult['edit']['result'] == 'Success':
                    logging.info('Creation of page %s succeeded' % pageTitle)
                else:
                    logging.error('Creation of page %s failed: %s' % (pageTitle, editResult.text))
        elif len(langs) == 1:
            dixNames = getDixNames(svnURL + pair, 'dix')
            lexcNames = getDixNames(svnURL + pair, 'lexc')

            dixCounts = {}
            for dixName in dixNames:
                counts = getCounts('{}{}/{}'.format(svnURL, pair, dixName), 'monodix')
                for countType, count in counts.items():
                    dixCounts[countType] = count
            for lexcName in lexcNames:
                counts = getCounts('{}{}/{}'.format(svnURL, pair, lexcName), 'lexc')
                for countType, count in counts.items():
                    dixCounts[countType] = count
            logging.info('Acquired dictionary counts %s' % dixCounts)
            
            pageContents = getPage(pageTitle)
            if pageContents:
                matchAttempts = re.finditer(r'^\*[^<]+(<section begin=([^/]+)/>.*?$)', pageContents, re.MULTILINE)
                replacements = {}
                for matchAttempt in matchAttempts:
                    countName = matchAttempt.group(2).strip()
                    if countName in dixCounts:
                        replacement = "<section begin={0} />{1:,d}<section end={0} /> as of r{2} ~ ~~~~".format(countName, dixCounts[countName], SVNRevision)
                        replacements[(matchAttempt.group(1))] = replacement
                for old, new in replacements.items():
                    pageContents = pageContents.replace(old, new)

                editResult = editPage(pageTitle, pageContents, editToken)
                if editResult['edit']['result'] == 'Success':
                    logging.info('Update of page %s succeeded' % pageTitle)
                else:
                    logging.error('Update of page %s failed: %s' % (pageTitle, editResult))
            else:
                pageContents = '==Over-all stats=='
                for dixName, dixCount in dixCounts.items():
                    pageContents += "\n*'''{0}''': <section begin={0} />{1:,d}<section end={0} /> as of r{2} ~ ~~~~".format(dixName, dixCount, SVNRevision)
                    
                editResult = editPage(pageTitle, pageContents, editToken)
                if editResult['edit']['result'] == 'Success':
                    logging.info('Creation of page %s succeeded' % pageTitle)
                else:
                    logging.error('Creation of page %s failed: %s' % (pageTitle, editResult.text))
        else:
            raise NotImplementedError