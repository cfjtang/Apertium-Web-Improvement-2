#!/usr/bin/env python3

baseURL = 'http://wiki.apertium.org/w/api.php'
svnURL = 'https://svn.code.sf.net/p/apertium/svn/'
lexccounterURL = svnURL + 'trunk/apertium-tools/lexccounter.py'

import argparse, requests, json, logging, sys, re, os, subprocess, shutil, importlib, urllib.request, collections, tempfile
import xml.etree.ElementTree as etree
try:
    from lexccounter import countStems
except ImportError:
    fullpath, _ = urllib.request.urlretrieve(lexccounterURL)
    path, filename = os.path.split(fullpath)
    filename, ext = os.path.splitext(filename)
    shutil.copyfile(fullpath, os.path.join(path, os.path.join(filename + '.py')))
    sys.path.append(path)
    lexccounter = importlib.import_module(filename)
    countStems = vars(lexccounter)['countStems']

def getCounts(uri, fileFormat):
    logger = logging.getLogger("countStems")
    try:
        if fileFormat == 'monodix':
            fileString = str((urllib.request.urlopen(uri)).read(), 'utf-8')
            dixTree = etree.fromstring(fileString)
            return {
                'stems': len(dixTree.findall("section/*[@lm]")),
                'paradigms': len(dixTree.find('pardefs').findall("pardef"))
            }
        elif fileFormat == 'bidix':
            fileString = str((urllib.request.urlopen(uri)).read(), 'utf-8')
            dixTree = etree.fromstring(fileString)
            return {
                'stems': len(dixTree.findall("*[@id='main']/e//l"))
            }
        elif fileFormat == 'lexc':
            logger = logging.getLogger("countStems")
            logger.setLevel(logging.ERROR)
            fileString = str((urllib.request.urlopen(uri)).read(), 'utf-8')
            return {
                'stems': countStems(fileString),
                'vanilla stems': countStems(fileString, vanilla=True)
            }
        elif fileFormat == 'rlx':
            return {
                'rlx-rules': countRlxRules(uri)
            }
        else:
            raise ValueError('Invalid format: %s' % fileFormat)
    except Exception as e:
        logger.error('Unable to parse counts from %s: %s' % (uri, str(e)))
        return {}

def getFileLocs(pair, fileFormat):
    locations = ['incubator', 'trunk', 'languages', 'nursery', 'staging']
    for location in locations:
        try:
            URL = svnURL + location + '/apertium-' + pair
            logging.debug('Finding %s files in %s' % (fileFormat, location))
            svnData = str(subprocess.check_output('svn list --xml %s' % URL, stderr=subprocess.STDOUT, shell=True), 'utf-8')
            return [URL + '/' + fileName for fileName in re.findall(r'<name>([^\.]+\.[^\.]+\.(?:meta)?(?:post)?%s)</name>' % fileFormat, svnData, re.DOTALL)]
        except subprocess.CalledProcessError:
            pass
    logging.error('No %s files found for %s' % (fileFormat, pair))
    return []

def getRevisionInfo(uri):
    try:
        svnData = str(subprocess.check_output('svn info -r HEAD %s --xml' % uri, stderr=subprocess.STDOUT, shell=True), 'utf-8')
        revisionNumber = re.findall(r'revision="([0-9]+)"', svnData, re.DOTALL)[1]
        revisionAuthor = re.findall(r'<author>(.*)</author>', svnData, re.DOTALL)[0]
        return (revisionNumber, revisionAuthor)
    except:
        return None

def createStatsSection(fileCounts):
    statsSection = '==Over-all stats=='
    for countName in sorted(fileCounts.keys()):
        count, revisionInfo, fileUrl = fileCounts[countName]
        statsSection += '\n' + createStatSection(countName, count, revisionInfo, fileUrl)
    return statsSection

def createStatSection(countName, count, revisionInfo, fileUrl):
    return "*'''[{5} {0}]''': <section begin={1} />{2:,d}<section end={1} /> as of r{3} by {4} ~ ~~~~".format(countName, countName.replace(' ', '_'), count, revisionInfo[0], revisionInfo[1], fileUrl)

def countRlxRules(url):
    f = tempfile.NamedTemporaryFile()
    urllib.request.urlretrieve(url, f.name)
    try:
        try:
            compilationOutput = str(subprocess.check_output('cg-comp %s %s' % (f.name, '/dev/null'), stderr=subprocess.STDOUT, shell=True), 'utf-8')
            return int(re.search(r'Rules: (\d+)', compilationOutput).group(1))
        except subprocess.CalledProcessError as e:
            if e.output.decode('utf-8'):
                return int(re.search(r'Rules: (\d+)', e.output.decode('utf-8')).group(1))
            else:
                logging.error('Unable to count rules from %s: %s' % (url, str(e)))
    except Exception as e:
        logging.error('Unable to count rules from %s: %s' % (url, str(e)))

def addCategory(pageContents):
    categoryMarker = '[[Category:Datastats]]'
    if categoryMarker in pageContents:
        return pageContents
    else:
        logging.debug('Adding category marker (%s)' % categoryMarker)
        return pageContents + '\n' * 3 + categoryMarker 

def getPage(pageTitle):
    payload = {'action': 'query', 'format': 'json', 'titles': pageTitle, 'prop': 'revisions', 'rvprop': 'content'}
    viewResult = s.get(baseURL, params=payload)
    jsonResult = json.loads(viewResult.text)

    if not 'missing' in list(jsonResult['query']['pages'].values())[0]:
        return list(jsonResult['query']['pages'].values())[0]['revisions'][0]['*']

def editPage(pageTitle, pageContents, editToken):
    if pageContents.find('[[Category:Datastats]]') < 0:
        pageContents += "\n\n[[Category:Datastats]]"
        logging.debug('Putting page in Category:Datastats')

    payload = {'action': 'edit', 'format': 'json', 'title': pageTitle, 'text': pageContents, 'bot': 'True', 'contentmodel': 'wikitext', 'token': editToken}
    editResult = s.post(baseURL, params=payload)
    jsonResult = json.loads(editResult.text)

    return jsonResult

def login(loginName, password):
    try:
        payload = {'action': 'login', 'format': 'json', 'lgname': loginName, 'lgpassword': password}
        authResult = s.post(baseURL, params=payload)
        authToken = json.loads(authResult.text)['login']['token']
        logging.debug('Auth token: %s' % authToken)

        payload = {'action': 'login', 'format': 'json', 'lgname': args.loginName, 'lgpassword': args.password, 'lgtoken': authToken}
        authResult = s.post(baseURL, params=payload)
        if not json.loads(authResult.text)['login']['result'] == 'Success':
            logging.critical('Failed to login as %s: %s' % (args.loginName, json.loads(authResult.text)['login']['result']))
        else:
            logging.info('Login as %s succeeded' % args.loginName)
            return authToken
    except Exception as e:
        logging.critical('Failed to login: %s' % e)

def getToken(tokenType, props):
    try:
        payload = {'action': 'query', 'format': 'json', 'prop': props, 'intoken': tokenType, 'titles':'Main Page'}
        tokenResult = s.get(baseURL, params=payload)
        token = json.loads(tokenResult.text)['query']['pages']['1']['%stoken' % tokenType]
        logging.debug('%s token: %s' % (tokenType, token))
        return token
    except Exception as e:
        logging.error('Failed to obtain %s token: %s' % (tokenType, e))

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Apertium Wiki Bot")
    parser.add_argument('loginName', help="bot login name")
    parser.add_argument('password', help="bot password")
    parser.add_argument('action', help="action for bot to perform", choices=['dict', 'coverage'])
    parser.add_argument('-p', '--pairs', nargs='+', help="Apertium language pairs in the format e.g. bg-ru or rus")
    parser.add_argument('-a', '--analyzers', nargs='+', help="Apertium analyzers (.automorf.bin files)")
    parser.add_argument('-v', '--verbose', help="show errors dictionary (verbose)", action='store_true', default=False)

    args = parser.parse_args()
    if args.action == 'dict' and not args.pairs:
        parser.error('action "dict" requires pairs (-p, --pairs) argument')
    if args.action == 'coverage':
        if not args.pairs:
            parser.error('action "coverage" requires pairs (-p, --pairs) argument')
        elif not args.analyzers:
            parser.error('action "coverage" requires analyzers (-a, --analyzers) argument')
        elif not len(args.pairs) == len(args.analyzers):
            parser.error('action "coverage" requires --analyzers and --pairs to be the same length')

    s = requests.Session()

    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)
    else:
        logging.basicConfig(level=logging.INFO)

    authToken = login(args.loginName, args.password)
    moveToken = getToken('move', 'info')
    editToken = getToken('edit', 'info|revisions')
    if not all([authToken, moveToken, editToken]):
        logging.critical('Failed to obtain required token')
        sys.exit(-1)

    if args.action == 'dict':
        for pair in args.pairs:
            try:
                langs = pair.split('-')
                pageTitle = 'Apertium-' + '-'.join(langs) + '/stats'
            except:
                logging.error('Failed to parse language module name: %s' % pair)
                break

            if len(langs) == 2:
                fileLocs = getFileLocs(pair, 'dix') + getFileLocs(pair, 'rlx')
                logging.debug('Acquired file locations %s' % fileLocs)

                if len(fileLocs) > 0:
                    fileCounts = {}
                    for fileLoc in fileLocs:
                        filePair = fileLoc.split('/')[-1].split('.')[1].split('-')
                        fileFormat = 'rlx' if fileLoc.endswith('.rlx') else ('bidix' if set(langs) == set(filePair) else 'monodix')
                        counts = getCounts(fileLoc, fileFormat)
                        for countType, count in counts.items():
                            fileCounts['-'.join(filePair + [countType])] = (count, getRevisionInfo(fileLoc), fileLoc)
                    logging.debug('Acquired file counts %s' % fileCounts)

                    pageContents = getPage(pageTitle)
                    if pageContents:
                        statsSection = re.search(r'==\s*Over-all stats\s*==', pageContents, re.IGNORECASE)
                        if statsSection:
                            matchAttempts = re.finditer(r'^\*[^<]+(<section begin=([^/]+)/>.*?$)', pageContents, re.MULTILINE)
                            replacements = {}
                            for matchAttempt in matchAttempts:
                                countName = matchAttempt.group(2).strip()
                                if countName in fileCounts:
                                    count, (revisionNumber, revisionAuthor), fileUrl = fileCounts[countName]
                                    replacement = "<section begin={0} />{1:,d}<section end={0} /> as of r{2} by {3} ~ ~~~~".format(countName.replace(' ', '_'), count, revisionNumber, revisionAuthor)
                                    replacements[(matchAttempt.group(1))] = replacement
                                    del fileCounts[countName]
                                    logging.debug('Replaced count %s' % repr(countName))
                            for old, new in replacements.items():
                                pageContents = pageContents.replace(old, new)

                            newStats = ''
                            for countName in sorted(fileCounts.keys()):
                                count, revisionInfo, fileUrl = fileCounts[countName]
                                newStats += '\n' + createStatSection(countName, count, revisionInfo, fileUrl)
                                logging.debug('Adding new count %s' % repr(countName))
                            newStats += '\n'

                            contentBeforeIndex = statsSection.start()
                            contentAfterIndex = pageContents.find('==', statsSection.end() + 1) if pageContents.find('==', statsSection.end() + 1) != -1 else len(pageContents)
                            pageContents = pageContents[:contentBeforeIndex] + pageContents[contentBeforeIndex:contentAfterIndex].rstrip() + newStats + '\n' + pageContents[contentAfterIndex:]
                        else:
                            pageContents += '\n' + createStatsSection(fileCounts)
                            logging.debug('Adding new stats section')
                            pageContents = addCategory(pageContents)

                        editResult = editPage(pageTitle, pageContents, editToken)
                        if editResult['edit']['result'] == 'Success':
                            logging.info('Update of page %s succeeded' % pageTitle)
                        else:
                            logging.error('Update of page %s failed: %s' % (pageTitle, editResult))
                    else:
                        pageContents = createStatsSection(fileCounts)
                        pageContents = addCategory(pageContents)

                        editResult = editPage(pageTitle, pageContents, editToken)
                        if editResult['edit']['result'] == 'Success':
                            logging.info('Creation of page %s succeeded' % pageTitle)
                        else:
                            logging.error('Creation of page %s failed: %s' % (pageTitle, editResult.text))
            elif len(langs) == 1:
                dixLoc = next(iter(sorted(sorted(getFileLocs(pair, 'dix'), key=lambda x: collections.defaultdict(lambda _: -1, {'.postdix': 0, '.dix': 1, '.metadix': 2})[x[x.rfind('.'):]])[::-1], key=len)), None)
                lexcLoc = next(iter(getFileLocs(pair, 'lexc')), None)
                rlxLoc = next(iter(getFileLocs(pair, 'rlx')), None)

                if dixLoc or lexcLoc or rlxLoc:
                    logging.debug('Acquired file locations %s, %s, %s' % (dixLoc, lexcLoc, rlxLoc))

                    fileCounts = {}
                    if dixLoc and not lexcLoc:
                        counts = getCounts(dixLoc, 'monodix')
                        for countType, count in counts.items():
                            fileCounts[countType] = (count, getRevisionInfo(dixLoc), dixLoc)
                    if lexcLoc:
                        counts = getCounts(lexcLoc, 'lexc')
                        for countType, count in counts.items():
                            fileCounts[countType] = (count, getRevisionInfo(lexcLoc), lexcLoc)
                    if rlxLoc:
                        counts = getCounts(rlxLoc, 'rlx')
                        for countType, count in counts.items():
                            fileCounts[countType] = (count, getRevisionInfo(rlxLoc), rlxLoc)

                    logging.info('Acquired file counts %s' % fileCounts)

                    pageContents = getPage(pageTitle)
                    if pageContents:
                        statsSection = re.search(r'==\s*Over-all stats\s*==', pageContents, re.IGNORECASE)
                        if statsSection:
                            matchAttempts = re.finditer(r'^\*[^<]+(<section begin=([^/]+)/>.*?$)', pageContents, re.MULTILINE)
                            replacements = {}
                            for matchAttempt in matchAttempts:
                                countName = matchAttempt.group(2).strip()
                                if countName in fileCounts:
                                    count, (revisionNumber, revisionAuthor), fileUrl = fileCounts[countName]
                                    replacement = "<section begin={0} />{1:,d}<section end={0} /> as of r{2} by {3} ~ ~~~~".format(countName.replace(' ', '_'), count, revisionNumber, revisionAuthor)
                                    replacements[(matchAttempt.group(1))] = replacement
                                    del fileCounts[countName]
                                    logging.debug('Replaced count %s' % repr(countName))
                            for old, new in replacements.items():
                                pageContents = pageContents.replace(old, new)

                            newStats = ''
                            for countName in sorted(fileCounts.keys()):
                                count, revisionInfo, fileUrl = fileCounts[countName]
                                newStats += '\n' + createStatSection(countName, count, revisionInfo, fileUrl)
                                logging.debug('Adding new count %s' % repr(countName))
                            newStats += '\n'

                            contentBeforeIndex = statsSection.start()
                            contentAfterIndex = pageContents.find('==', statsSection.end() + 1) if pageContents.find('==', statsSection.end() + 1) != -1 else len(pageContents)
                            pageContents = pageContents[:contentBeforeIndex] + pageContents[contentBeforeIndex:contentAfterIndex].rstrip() + newStats + '\n' + pageContents[contentAfterIndex:]
                        else:
                            pageContents += '\n' + createStatsSection(fileCounts)
                            logging.debug('Adding new stats section')
                            pageContents = addCategory(pageContents)

                        editResult = editPage(pageTitle, pageContents, editToken)
                        if editResult['edit']['result'] == 'Success':
                            logging.info('Update of page %s succeeded' % pageTitle)
                        else:
                            logging.error('Update of page %s failed: %s' % (pageTitle, editResult))
                    else:
                        pageContents = createStatsSection(fileCounts)
                        pageContents = addCategory(pageContents)

                        editResult = editPage(pageTitle, pageContents, editToken)
                        if editResult['edit']['result'] == 'Success':
                            logging.info('Creation of page %s succeeded' % pageTitle)
                        else:
                            logging.error('Creation of page %s failed: %s' % (pageTitle, editResult.text))
            else:
                logging.error('Invalid language module name: %s' % pair)
    elif args.action == 'coverage':
       raise NotImplementedError

