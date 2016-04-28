#!/usr/bin/env python3

wikiURL = 'http://wiki.apertium.org/wiki/'
apiURL = 'http://wiki.apertium.org/w/api.php'
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

s = requests.Session()

def getCounts(uri, fileFormat):
    oldLoggingLevel = logging.getLogger().getEffectiveLevel()
    logging.getLogger().setLevel(logging.ERROR)

    oldStdout = sys.stdout
    sys.stdout = open(os.devnull, 'w')

    try:
        if fileFormat.endswith('dix'):
            fileString = str((urllib.request.urlopen(uri)).read(), 'utf-8')
            dixTree = etree.fromstring(fileString)

        if fileFormat == 'monodix':
            return {
                'stems': len(dixTree.findall("section/e[@lm]")),
                'paradigms': len(dixTree.find('pardefs').findall("pardef"))
            }
        elif fileFormat == 'metamonodix':
            return {
                'meta stems': len(dixTree.findall("section/e[@lm]")),
                'meta paradigms': len(dixTree.find('pardefs').findall("pardef"))
            }
        elif fileFormat == 'bidix':
            return {
                'stems': len(dixTree.findall("section/e"))
            }
        elif fileFormat == 'metabidix':
            return {
                'meta stems': len(dixTree.findall("section/e"))
            }
        elif fileFormat == 'lexc':
            logging.getLogger('countStems').setLevel(logging.ERROR)
            fileString = str((urllib.request.urlopen(uri)).read(), 'utf-8')
            return {
                'stems': countStems(fileString),
                'vanilla stems': countStems(fileString, vanilla=True)
            }
        elif fileFormat == 'rlx':
            return {
                'rlx rules': countRlxRules(uri)
            }
        elif re.match(r't\dx', fileFormat):
            fileString = str((urllib.request.urlopen(uri)).read(), 'utf-8')
            dixTree = etree.fromstring(fileString)
            return {
                fileFormat + ' rules': len(dixTree.findall('.//rule')),
                fileFormat + ' macros': len(dixTree.findall('.//macro'))
            }
        else:
            raise ValueError('Invalid format: %s' % fileFormat)
    except (Exception, SystemExit) as e:
        logging.getLogger().setLevel(oldLoggingLevel)
        logging.error('Unable to parse counts from %s: %s' % (uri, str(e)))
        return {}
    finally:
        logging.getLogger().setLevel(oldLoggingLevel)
        sys.stdout.close()
        sys.stdout = oldStdout

svnDataCache = {}

def getFileLocs(pair, fileFormat, includePost=False, locations=['trunk', 'languages', 'staging', 'nursery', 'incubator']):
    global svnDataCache

    try:
        incubatorURL = svnURL + 'incubator'
        if incubatorURL in svnDataCache:
            incubatorData = svnDataCache[incubatorURL]
            logging.debug('Using SVN data cache for incubator root list to find %s %s files' % (fileFormat, pair))
        else:
            incubatorData = str(subprocess.check_output('svn list --xml %s' % incubatorURL, stderr=subprocess.STDOUT, shell=True), 'utf-8')
            svnDataCache[incubatorURL] = incubatorData
    except subprocess.CalledProcessError as e:
        logging.error('Unable to retrieve incubator root list: %s' % e)
        incubatorData = ''

    if '-' in pair:
        pairParts = pair.split('-')
        incubatorRootMatches = re.findall(r'<name>(apertium-{0}\.{4}(?:{1}-{2}|{1}|{2})\.{3}(?:\.xml)?)</name>'.format(pair, pairParts[0], pairParts[1], fileFormat, '(?:post-)?' if includePost else ''), incubatorData, re.DOTALL)
    else:
        incubatorRootMatches = re.findall(r'<name>(apertium-{0}\.{2}{0}\.{1}(?:\.xml)?)</name>'.format(pair, fileFormat, '(?:post-)?' if includePost else ''), incubatorData, re.DOTALL)
    incubatorRootMatches = list(map(lambda x: incubatorURL + '/' + x, incubatorRootMatches))

    for location in locations:
        try:
            URL = svnURL + location + '/apertium-' + pair
            if URL in svnDataCache:
                svnData = svnDataCache[URL]
                logging.debug('Using SVN data cache for %s %s files in %s' % (fileFormat, pair, location))
            else:
                logging.debug('Finding %s files for %s in %s' % (fileFormat, pair, location))
                svnData = str(subprocess.check_output('svn list --xml %s' % URL, stderr=subprocess.STDOUT, shell=True), 'utf-8')
                svnDataCache[URL] = svnData

            return [URL + '/' + fileName for fileName in re.findall(r'<name>([^\.]+\.[^\.]+\.%s(?:\.xml)?)</name>' % fileFormat, svnData, re.DOTALL) if includePost or not fileName.split('.')[1].startswith('post')] + incubatorRootMatches
        except subprocess.CalledProcessError:
            pass

    if not incubatorRootMatches:
        logging.error('No %s files found for %s' % (fileFormat, pair))
    else:
        logging.info('Found %s files for %s in incubator root' % (fileFormat, pair))

    return [] + incubatorRootMatches

def getPairCounts(langs, fileLocs):
    fileCounts = {}
    for fileLoc in fileLocs:
        fileLangs = fileLoc.split('/')[-1].split('.')[1].split('-')
        if fileLoc.replace('.xml', '').endswith('.dix'):
            fileFormat = 'bidix' if set(map(toAlpha3Code, langs)) == set(map(toAlpha3Code, fileLangs)) else 'monodix'
        elif fileLoc.replace('.xml', '').endswith('.metadix'):
            fileFormat = 'metabidix' if set(map(toAlpha3Code, langs)) == set(map(toAlpha3Code, fileLangs)) else 'metamonodix'
        else:
            fileFormat = fileLoc.split('.')[-1]

        counts = getCounts(fileLoc, fileFormat)
        for countType, count in counts.items():
            if fileLoc.replace('.xml', '').endswith('metadix') and not list(filter(lambda x: x == fileLoc.replace('.xml', '').replace('.metadix', '.dix'), fileLocs)):
                countType = countType.replace('meta ', '')
                logging.debug('Assuming metadix %s as dix' % fileLoc)

            revisionInfo = getRevisionInfo(fileLoc)
            if revisionInfo:
                fileCounts['-'.join(fileLangs) + ' ' + countType] = (count, revisionInfo, fileLoc)

    return fileCounts

def getMonoLangCounts(dixLoc, lexcLoc, rlxLoc):
    fileCounts = {}

    if dixLoc or lexcLoc or rlxLoc:
        logging.debug('Acquired file locations %s, %s, %s' % (dixLoc, lexcLoc, rlxLoc))

        if dixLoc and not lexcLoc:
            counts = getCounts(dixLoc, 'monodix')
            for countType, count in counts.items():
                revisionInfo = getRevisionInfo(dixLoc)
                if revisionInfo:
                    fileCounts[countType] = (count, revisionInfo, dixLoc)
        if lexcLoc:
            counts = getCounts(lexcLoc, 'lexc')
            for countType, count in counts.items():
                revisionInfo = getRevisionInfo(lexcLoc)
                if revisionInfo:
                    fileCounts[countType] = (count, revisionInfo, lexcLoc)
        if rlxLoc:
            counts = getCounts(rlxLoc, 'rlx')
            for countType, count in counts.items():
                revisionInfo = getRevisionInfo(rlxLoc)
                if revisionInfo:
                    fileCounts[countType] = (count, revisionInfo, rlxLoc)

    return fileCounts

def getRevisionInfo(uri):
    try:
        svnData = str(subprocess.check_output('svn info -r HEAD %s --xml' % uri, stderr=subprocess.STDOUT, shell=True), 'utf-8')
        revisionNumber = re.findall(r'revision="([0-9]+)"', svnData, re.DOTALL)[1]
        revisionAuthor = re.findall(r'<author>(.*)</author>', svnData, re.DOTALL)[0]
        return (revisionNumber, revisionAuthor)
    except Exception as e:
        logging.error('Unable to get revision info for %s: %s' % (uri, e))
        return None

def createStatsSection(fileCounts, requester=None):
    statsSection = '==Over-all stats=='
    for countName in sorted(fileCounts.keys(), key=lambda countName: (fileCounts[countName] and fileCounts[countName][0] is 0, countName)):
        if fileCounts[countName]:
            count, revisionInfo, fileUrl = fileCounts[countName]
            statsSection += '\n' + createStatSection(countName, count, revisionInfo, fileUrl, requester=requester)
        else:
            statsSection += "\n*'''{0}''': <section begin={1} />?<section end={1} /> ~ ~~~~".format(countName, countName.replace(' ', '_'))
            if requester:
                statsSection += ', run by %s' % requester
    return statsSection

def createStatSection(countName, count, revisionInfo, fileUrl, requester=None):
    if count is 0:
        statSection = "*<span style='opacity: .6'>'''[{5} {0}]''': <section begin={1} />{2:,d}<section end={1} /> as of r{3} by {4} ~ ~~~~".format(countName, countName.replace(' ', '_'), count, revisionInfo[0], revisionInfo[1], fileUrl)
    else:
        statSection = "*'''[{5} {0}]''': <section begin={1} />{2:,d}<section end={1} /> as of r{3} by {4} ~ ~~~~".format(countName, countName.replace(' ', '_'), count, revisionInfo[0], revisionInfo[1], fileUrl)

    if requester:
        statSection += ', run by %s' % requester

    if count is 0:
        statSection += '</span>'

    return statSection

def updatePairStatsSection(statsSection, pageContents, fileCounts, requester=None):
    matchAttempts = re.finditer(r'(^\*.*?<section begin=([^/]+)/>.*?$)', pageContents, re.MULTILINE)
    replacements = {}
    for matchAttempt in matchAttempts:
        countName = matchAttempt.group(2).strip().replace('_', ' ')
        if countName in fileCounts:
            count, revisionInfo, fileUrl = fileCounts[countName]
            replacement = createStatSection(countName, count, revisionInfo, fileUrl, requester=requester)
            replacements[(matchAttempt.group(1))] = replacement
            del fileCounts[countName]
            logging.debug('Replaced count %s' % repr(countName))
        else:
            langPairEndIndex = countName.find('-', countName.find('-') + 1)
            oldLangPairEntry = langPairEndIndex != -1 and countName[:langPairEndIndex] + countName[langPairEndIndex:].replace('-', ' ') in fileCounts
            oldLangEntry = countName.count('-') == 1 and countName.replace('-', ' ') in fileCounts
            postEntry = countName.startswith('post')
            if oldLangEntry or oldLangPairEntry or postEntry:
                replacements[(matchAttempt.group(1))] = ''
                logging.debug('Deleting old style count %s' % repr(countName))

    for old, new in replacements.items():
        if new == '':
            pageContents = pageContents.replace(old + '\n', new)
        pageContents = pageContents.replace(old, new)

    newStats = ''
    for countName in sorted(fileCounts.keys(), key=lambda countName: (fileCounts[countName] and fileCounts[countName][0] is 0, countName)):
        if fileCounts[countName]:
            count, revisionInfo, fileUrl = fileCounts[countName]
            newStats += '\n' + createStatSection(countName, count, revisionInfo, fileUrl, requester=requester)
        else:
            newStats += "\n*'''{0}''': <section begin={1} />?<section end={1} /> ~ ~~~~".format(countName, countName.replace(' ', '_'))
            if requester:
                newStats += ', run by %s' % requester
        logging.debug('Adding new count %s' % repr(countName))
    newStats += '\n'

    contentBeforeIndex = statsSection.start()
    contentAfterIndex = pageContents.find('==', statsSection.end() + 1) if pageContents.find('==', statsSection.end() + 1) != -1 else len(pageContents)
    pageContents = pageContents[:contentBeforeIndex] + pageContents[contentBeforeIndex:contentAfterIndex].rstrip() + newStats + '\n' + pageContents[contentAfterIndex:]

    return pageContents

def updateMonoLangStatsSection(statsSection, pageContents, fileCounts, requester=None):
    matchAttempts = re.finditer(r'(^\*.*?<section begin=([^/]+)/>.*?$)', pageContents, re.MULTILINE)
    replacements = {}
    for matchAttempt in matchAttempts:
        countName = matchAttempt.group(2).strip().replace('_', ' ')
        if countName in fileCounts:
            count, revisionInfo, fileUrl = fileCounts[countName]
            replacement = createStatSection(countName, count, revisionInfo, fileUrl, requester=args.requester)
            replacements[(matchAttempt.group(1))] = replacement
            del fileCounts[countName]
            logging.debug('Replaced count %s' % repr(countName))
    for old, new in replacements.items():
        pageContents = pageContents.replace(old, new)

    newStats = ''
    for countName in sorted(fileCounts.keys(), key=lambda countName: (fileCounts[countName] and fileCounts[countName][0] is 0, countName)):
        count, revisionInfo, fileUrl = fileCounts[countName]
        newStats += '\n' + createStatSection(countName, count, revisionInfo, fileUrl, requester=args.requester)
        logging.debug('Adding new count %s' % repr(countName))
    newStats += '\n'

    contentBeforeIndex = statsSection.start()
    contentAfterIndex = pageContents.find('==', statsSection.end() + 1) if pageContents.find('==', statsSection.end() + 1) != -1 else len(pageContents)
    pageContents = pageContents[:contentBeforeIndex] + pageContents[contentBeforeIndex:contentAfterIndex].rstrip() + newStats + '\n' + pageContents[contentAfterIndex:]

    return pageContents

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

def toAlpha3Code(code):
    iso639Codes = {"abk":"ab","aar":"aa","afr":"af","aka":"ak","sqi":"sq","amh":"am","ara":"ar","arg":"an","hye":"hy","asm":"as","ava":"av","ave":"ae","aym":"ay","aze":"az","bam":"bm","bak":"ba","eus":"eu","bel":"be","ben":"bn","bih":"bh","bis":"bi","bos":"bs","bre":"br","bul":"bg","mya":"my","cat":"ca","cha":"ch","che":"ce","nya":"ny","zho":"zh","chv":"cv","cor":"kw","cos":"co","cre":"cr","hrv":"hr","ces":"cs","dan":"da","div":"dv","nld":"nl","dzo":"dz","eng":"en","epo":"eo","est":"et","ewe":"ee","fao":"fo","fij":"fj","fin":"fi","fra":"fr","ful":"ff","glg":"gl","kat":"ka","deu":"de","ell":"el","grn":"gn","guj":"gu","hat":"ht","hau":"ha","heb":"he","her":"hz","hin":"hi","hmo":"ho","hun":"hu","ina":"ia","ind":"id","ile":"ie","gle":"ga","ibo":"ig","ipk":"ik","ido":"io","isl":"is","ita":"it","iku":"iu","jpn":"ja","jav":"jv","kal":"kl","kan":"kn","kau":"kr","kas":"ks","kaz":"kk","khm":"km","kik":"ki","kin":"rw","kir":"ky","kom":"kv","kon":"kg","kor":"ko","kur":"ku","kua":"kj","lat":"la","ltz":"lb","lug":"lg","lim":"li","lin":"ln","lao":"lo","lit":"lt","lub":"lu","lav":"lv","glv":"gv","mkd":"mk","mlg":"mg","msa":"ms","mal":"ml","mlt":"mt","mri":"mi","mar":"mr","mah":"mh","mon":"mn","nau":"na","nav":"nv","nob":"nb","nde":"nd","nep":"ne","ndo":"ng","nno":"nn","nor":"no","iii":"ii","nbl":"nr","oci":"oc","oji":"oj","chu":"cu","orm":"om","ori":"or","oss":"os","pan":"pa","pli":"pi","fas":"fa","pol":"pl","pus":"ps","por":"pt","que":"qu","roh":"rm","run":"rn","ron":"ro","rus":"ru","san":"sa","srd":"sc","snd":"sd","sme":"se","smo":"sm","sag":"sg","srp":"sr","gla":"gd","sna":"sn","sin":"si","slk":"sk","slv":"sl","som":"so","sot":"st","azb":"az","spa":"es","sun":"su","swa":"sw","ssw":"ss","swe":"sv","tam":"ta","tel":"te","tgk":"tg","tha":"th","tir":"ti","bod":"bo","tuk":"tk","tgl":"tl","tsn":"tn","ton":"to","tur":"tr","tso":"ts","tat":"tt","twi":"tw","tah":"ty","uig":"ug","ukr":"uk","urd":"ur","uzb":"uz","ven":"ve","vie":"vi","vol":"vo","wln":"wa","cym":"cy","wol":"wo","fry":"fy","xho":"xh","yid":"yi","yor":"yo","zha":"za","zul":"zu", "hbs":"sh", "arg":"an", "pes":"fa"}
    '''
        Bootstrapped from https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes using
            var out = {};
            $.each($('tr', $('table').get(1)), function(i, elem) { var rows = $('td', elem); out[$(rows[5]).text()] = $(rows[4]).text(); });
            JSON.stringify(out);
    '''

    iso639CodesInverse = {v: k for k, v in iso639Codes.items()}
    if '_' in code:
        code, variant = code.split('_')
        return '%s_%s' % ((iso639CodesInverse[code], variant) if code in iso639CodesInverse else  (code, variant))
    else:
        return iso639CodesInverse[code] if code in iso639CodesInverse else code

def getPage(pageTitle):
    payload = {'action': 'query', 'format': 'json', 'titles': pageTitle, 'prop': 'revisions', 'rvprop': 'content'}
    viewResult = s.get(apiURL, params=payload)
    jsonResult = json.loads(viewResult.text)

    if not 'missing' in list(jsonResult['query']['pages'].values())[0]:
        return list(jsonResult['query']['pages'].values())[0]['revisions'][0]['*']

def editPage(pageTitle, pageContents, editToken):
    payload = {'action': 'edit', 'format': 'json', 'title': pageTitle, 'text': pageContents, 'bot': 'True', 'contentmodel': 'wikitext', 'token': editToken}
    editResult = s.post(apiURL, data=payload)
    jsonResult = json.loads(editResult.text)
    return jsonResult

def login(loginName, password):
    try:
        payload = {'action': 'login', 'format': 'json', 'lgname': loginName, 'lgpassword': password}
        authResult = s.post(apiURL, params=payload)
        authToken = json.loads(authResult.text)['login']['token']
        logging.debug('Auth token: %s' % authToken)

        payload = {'action': 'login', 'format': 'json', 'lgname': loginName, 'lgpassword': password, 'lgtoken': authToken}
        authResult = s.post(apiURL, params=payload)
        if not json.loads(authResult.text)['login']['result'] == 'Success':
            logging.critical('Failed to login as %s: %s' % (loginName, json.loads(authResult.text)['login']['result']))
        else:
            logging.info('Login as %s succeeded' % loginName)
            return authToken
    except Exception as e:
        logging.critical('Failed to login: %s' % e)

def getToken(tokenType, props):
    try:
        payload = {'action': 'query', 'format': 'json', 'prop': props, 'intoken': tokenType, 'titles':'Main Page'}
        tokenResult = s.get(apiURL, params=payload)
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
    parser.add_argument('-p', '--pairs', nargs='+', help="Apertium language pairs/monolingual packages in the format e.g. bg-ru or rus")
    parser.add_argument('-a', '--analyzers', nargs='+', help="Apertium analyzers (.automorf.bin files)")
    parser.add_argument('-v', '--verbose', help="verbose mode (debug)", action='store_true', default=False)
    parser.add_argument('-r', '--requester', help="user who requests update", default=None)

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
                fileLocs = sum(map(lambda x: getFileLocs(pair, x), ['dix', 'metadix', 'lexc', 'rlx', 't\dx']), [])
                logging.debug('Acquired file locations %s' % fileLocs)

                if len(fileLocs) is 0:
                    logging.error('No files found for %s, adding placeholder bidix stems entry.' % repr(langs))
                    fileCounts = {'%s-%s stems' % tuple(langs): None}
                else:
                    fileCounts = getPairCounts(langs, fileLocs)
                    logging.debug('Acquired file counts %s' % fileCounts)

                    if len(fileCounts) is 0:
                        logging.error('No file counts available for %s, adding placeholder bidix stems entry.' % repr(langs))
                        fileCounts = {'%s-%s stems' % tuple(langs): None}

                pageContents = getPage(pageTitle)
                if pageContents:
                    statsSection = re.search(r'==\s*Over-all stats\s*==', pageContents, re.IGNORECASE)
                    if statsSection:
                        pageContents = updatePairStatsSection(statsSection, pageContents, fileCounts, requester=args.requester)
                    else:
                        pageContents += '\n' + createStatsSection(fileCounts, requester=args.requester)
                        logging.debug('Adding new stats section')

                    pageContents = addCategory(pageContents)
                    editResult = editPage(pageTitle, pageContents, editToken)
                    if editResult['edit']['result'] == 'Success':
                        logging.info('Update of page {0} succeeded ({1}{0})'.format(pageTitle, wikiURL))
                    else:
                        logging.error('Update of page %s failed: %s' % (pageTitle, editResult))
                else:
                    pageContents = createStatsSection(fileCounts, requester=args.requester)
                    pageContents = addCategory(pageContents)

                    editResult = editPage(pageTitle, pageContents, editToken)
                    if editResult['edit']['result'] == 'Success':
                        logging.info('Creation of page {0} succeeded ({1}{0})'.format(pageTitle, wikiURL))
                    else:
                        logging.error('Creation of page %s failed: %s' % (pageTitle, editResult.text))

            elif len(langs) == 1:
                dixLoc = next(iter(sorted(sorted(getFileLocs(pair, 'dix'), key=lambda x: collections.defaultdict(lambda _: -1, {'.postdix': 0, '.dix': 1, '.metadix': 2})[x[x.rfind('.'):]])[::-1], key=len)), None)
                lexcLoc = next(iter(getFileLocs(pair, 'lexc')), None)
                rlxLoc = next(iter(getFileLocs(pair, 'rlx')), None)

                fileCounts = getMonoLangCounts(dixLoc, lexcLoc, rlxLoc)
                logging.info('Acquired file counts %s' % fileCounts)

                if fileCounts:
                    pageContents = getPage(pageTitle)
                    if pageContents:
                        statsSection = re.search(r'==\s*Over-all stats\s*==', pageContents, re.IGNORECASE)
                        if statsSection:
                            pageContents = updateMonoLangStatsSection(statsSection, pageContents, fileCounts, requester=args.requester)
                        else:
                            pageContents += '\n' + createStatsSection(fileCounts, requester=args.requester)
                            logging.debug('Adding new stats section')

                        pageContents = addCategory(pageContents)
                        editResult = editPage(pageTitle, pageContents, editToken)
                        if editResult['edit']['result'] == 'Success':
                            logging.info('Update of page {0} succeeded ({1}{0})'.format(pageTitle, wikiURL))
                        else:
                            logging.error('Update of page %s failed: %s' % (pageTitle, editResult))
                    else:
                        pageContents = createStatsSection(fileCounts, requester=args.requester)
                        pageContents = addCategory(pageContents)

                        editResult = editPage(pageTitle, pageContents, editToken)
                        if editResult['edit']['result'] == 'Success':
                            logging.info('Creation of page {0} succeeded ({1}{0})'.format(pageTitle, wikiURL))
                        else:
                            logging.error('Creation of page %s failed: %s' % (pageTitle, editResult.text))
                else:
                    logging.error('No file counts available for %s, skipping.' % repr(langs))
            else:
                logging.error('Invalid language module name: %s' % pair)
    elif args.action == 'coverage':
       raise NotImplementedError
