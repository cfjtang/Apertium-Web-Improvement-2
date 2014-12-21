#!/usr/bin/env python3

import sys, os, argparse, itertools, re, subprocess, urllib.request, logging, collections
import xml.etree.ElementTree as etree

iso639Codes = {'roh': 'rm', 'gv': 'glv', 'gu': 'guj', 'ron': 'ro', 'oss': 'os', 'gd': 'gla', 'nld': 'nl', 'ga': 'gle', 'se': 'sme', 'gl': 'glg', 'oji': 'oj', 'oci': 'oc', 'ty': 'tah', 'jav': 'jv', 'tw': 'twi', 'tt': 'tat', 'hrv': 'hr', 'tr': 'tur', 'ts': 'tso', 'tn': 'tsn', 'to': 'ton', 'tl': 'tgl', 'tk': 'tuk', 'th': 'tha', 'ti': 'tir', 'ven': 've', 'tg': 'tgk', 'te': 'tel', 'ta': 'tam', 'fas': 'fa', 'ssw': 'ss', 'de': 'deu', 'da': 'dan', 'ay': 'aym', 'dz': 'dzo', 'fao': 'fo', 'dv': 'div', 'rn': 'run', 'hin': 'hi', 'qu': 'que', 'hye': 'hy', 'guj': 'gu', 'kua': 'kj', 'cre': 'cr', 'div': 'dv', 'bam': 'bm', 'bak': 'ba', 'tel': 'te', 'mi': 'mri', 'za': 'zha', 'mh': 'mah', 'ara': 'ar', 'ce': 'che', 'nbl': 'nr', 'zu': 'zul', 'wa': 'wln', 'sun': 'su', 'abk': 'ab', 'kur': 'ku', 'wol': 'wo', 'lub': 'lu', 'gn': 'grn', 'lug': 'lg', 'jv': 'jav', 'nep': 'ne', 'ms': 'msa', 'iku': 'iu', 'lg': 'lug', 'wo': 'wol', 'tur': 'tr', 'mr': 'mar', 'tuk': 'tk', 'ja': 'jpn', 'cos': 'co', 'ile': 'ie', 'gla': 'gd', 'bos': 'bs', 'gle': 'ga', 'glg': 'gl', 'aka': 'ak', 'bod': 'bo', 'glv': 'gv', 'aa': 'aar', 'ch': 'cha', 'co': 'cos', 'vie': 'vi', 'ipk': 'ik', 'ca': 'cat', 'bs': 'bos', 'por': 'pt', 'uzb': 'uz', 'na': 'nau', 'pol': 'pl', 'cs': 'ces', 'tgk': 'tg', 'bre': 'br', 'cv': 'chv', 'tgl': 'tl', 'aym': 'ay', 'cha': 'ch', 'fra': 'fr', 'che': 'ce', 'pt': 'por', 'swa': 'sw', 'twi': 'tw', 'swe': 'sv', 'pa': 'pan', 'chu': 'cu', 'chv': 'cv', 'vi': 'vie', 'fry': 'fy', 'pi': 'pli', 'msa': 'ms', 'am': 'amh', 'hmo': 'ho', 'iii': 'ii', 'ml': 'mal', 'mg': 'mlg', 'mlg': 'mg', 'ibo': 'ig', 'hat': 'ht', 'slv': 'sl', 'mn': 'mon', 'xho': 'xh', 'deu': 'de', 'mk': 'mkd', 'cat': 'ca', 'mt': 'mlt', 'mlt': 'mt', 'slk': 'sk', 'ful': 'ff', 'my': 'mya', 'tat': 'tt', 've': 'ven', 'jpn': 'ja', 'vol': 'vo', 'oc': 'oci', 'is': 'isl', 'iu': 'iku', 'it': 'ita', 'vo': 'vol', 'ii': 'iii', 'mya': 'my', 'ik': 'ipk', 'io': 'ido', 'spa': 'es', 'ia': 'ina', 'ave': 'ae', 'tah': 'ty', 'ava': 'av', 'ig': 'ibo', 'yo': 'yor', 'eng': 'en', 'ie': 'ile', 'ewe': 'ee', 'id': 'ind', 'nya': 'ny', 'sin': 'si', 'pan': 'pa', 'snd': 'sd', 'mar': 'mr', 'sna': 'sn', 'kir': 'ky', 'kik': 'ki', 'fa': 'fas', 'kin': 'rw', 'ff': 'ful', 'lat': 'la', 'mah': 'mh', 'lav': 'lv', 'mal': 'ml', 'fo': 'fao', 'ss': 'ssw', 'sr': 'srp', 'sq': 'sqi', 'sw': 'swa', 'sv': 'swe', 'su': 'sun', 'st': 'sot', 'sk': 'slk', 'epo': 'eo', 'si': 'sin', 'so': 'som', 'sn': 'sna', 'sm': 'smo', 'sl': 'slv', 'sc': 'srd', 'sa': 'san', 'ido': 'io', 'sg': 'sag', 'nb': 'nob', 'tha': 'th', 'sd': 'snd', 'ita': 'it', 'tsn': 'tn', 'tso': 'ts', 'lb': 'ltz', 'ell': 'el', 'la': 'lat', 'ln': 'lin', 'lo': 'lao', 'li': 'lim', 'lv': 'lav', 'lt': 'lit', 'lu': 'lub', 'fij': 'fj', 'fin': 'fi', 'hau': 'ha', 'eus': 'eu', 'yi': 'yid', 'amh': 'am', 'bih': 'bh', 'dan': 'da', 'nob': 'nb', 'ces': 'cs', 'mon': 'mn', 'bis': 'bi', 'nor': 'no', 'cy': 'cym', 'afr': 'af', 'el': 'ell', 'eo': 'epo', 'en': 'eng', 'ee': 'ewe', 'fr': 'fra', 'lao': 'lo', 'cr': 'cre', 'eu': 'eus', 'et': 'est', 'es': 'spa', 'ru': 'rus', 'est': 'et', 'smo': 'sm', 'cu': 'chu', 'fy': 'fry', 'rm': 'roh', 'sme': 'se', 'ro': 'ron', 'be': 'bel', 'bg': 'bul', 'run': 'rn', 'ba': 'bak', 'ps': 'pus', 'bm': 'bam', 'bn': 'ben', 'bo': 'bod', 'bh': 'bih', 'bi': 'bis', 'orm': 'om', 'que': 'qu', 'br': 'bre', 'ori': 'or', 'rus': 'ru', 'pli': 'pi', 'pus': 'ps', 'om': 'orm', 'oj': 'oji', 'srd': 'sc', 'ltz': 'lb', 'nde': 'nd', 'dzo': 'dz', 'ndo': 'ng', 'srp': 'sr', 'wln': 'wa', 'isl': 'is', 'os': 'oss', 'or': 'ori', 'zul': 'zu', 'xh': 'xho', 'som': 'so', 'sot': 'st', 'fi': 'fin', 'zh': 'zho', 'fj': 'fij', 'yid': 'yi', 'mkd': 'mk', 'kom': 'kv', 'her': 'hz', 'kon': 'kg', 'ukr': 'uk', 'ton': 'to', 'heb': 'he', 'kor': 'ko', 'hz': 'her', 'hy': 'hye', 'hr': 'hrv', 'hun': 'hu', 'ht': 'hat', 'hu': 'hun', 'hi': 'hin', 'ho': 'hmo', 'bul': 'bg', 'ha': 'hau', 'cym': 'cy', 'he': 'heb', 'ben': 'bn', 'bel': 'be', 'uz': 'uzb', 'azb': 'az', 'aze': 'az', 'ur': 'urd', 'zha': 'za', 'pl': 'pol', 'uk': 'ukr', 'aar': 'aa', 'ug': 'uig', 'zho': 'zh', 'nno': 'nn', 'ab': 'abk', 'ae': 'ave', 'san': 'sa', 'uig': 'ug', 'af': 'afr', 'ak': 'aka', 'arg': 'an', 'sag': 'sg', 'an': 'arg', 'as': 'asm', 'ar': 'ara', 'khm': 'km', 'av': 'ava', 'ind': 'id', 'az': 'aze', 'ina': 'ia', 'asm': 'as', 'nl': 'nld', 'nn': 'nno', 'no': 'nor', 'lim': 'li', 'lin': 'ln', 'nd': 'nde', 'ne': 'nep', 'tir': 'ti', 'ng': 'ndo', 'lit': 'lt', 'ny': 'nya', 'nav': 'nv', 'nau': 'na', 'grn': 'gn', 'nr': 'nbl', 'yor': 'yo', 'nv': 'nav', 'kv': 'kom', 'tam': 'ta', 'cor': 'kw', 'kan': 'kn', 'kal': 'kl', 'kas': 'ks', 'sqi': 'sq', 'rw': 'kin', 'kau': 'kr', 'kat': 'ka', 'kaz': 'kk', 'urd': 'ur', 'ka': 'kat', 'kg': 'kon', 'kk': 'kaz', 'kj': 'kua', 'ki': 'kik', 'ko': 'kor', 'kn': 'kan', 'km': 'khm', 'kl': 'kal', 'ks': 'kas', 'kr': 'kau', 'kw': 'cor', 'mri': 'mi', 'ku': 'kur', 'ky': 'kir', 'hbs': 'sh', 'sh': 'hbs'}

def iso639Code(code):
    return iso639_2Code(code), iso639_3Code(code)

def iso639_3Code(code):
    if len(code) < 3 and code in iso639Codes:
        return iso639Codes[code]
    else:
        return code

def iso639_2Code(code):
    if len(code) > 2 and code in iso639Codes:
        return iso639Codes[code]
    else:
        return code

def comprehensiveList(langs):
    newLangs = []
    for lang in langs:
        if lang in iso639Codes:
            newLangs.append(iso639Codes[lang])
    return set(langs + newLangs)

def updateOrCreateStatsPage(location, pairName, updateStats, editToken):
    pageTitle = pairName.capitalize() + '/stats'

    if not updateStats:
        page = getPage(pageTitle)

        lang1, lang2 = pairName.split('-')[1:]
        possiblePairs = list(itertools.product(iso639Code(lang1), iso639Code(lang2))) + list(itertools.product(iso639Code(lang2), iso639Code(lang1)))

        if page:
            stemEntryPresent = False
            for possiblePair in possiblePairs:
                if updateStats:
                    break
                else:
                    stemEntryPresent |= '<section begin={0}-{1}_stems'.format(*possiblePair) in page

            if not stemEntryPresent:
                updateStats = True
                logging.info('Bidix stem entry missing on %s stats page' % pairName)
        else:
            updateStats = True
            logging.info('Unable to find %s stats page' % pairName)

    if updateStats:
        logging.info('Updating %s stats page' % pairName)
        fileLocs = sum(map(lambda x: getFileLocs('-'.join(pairName.split('-')[1:]), x, locations=[location]), ['dix', 'metadix', 'lexc', 'rlx', 't\dx']), [])
        logging.debug('Acquired file locations %s' % fileLocs)

        if len(fileLocs) > 0:
            fileCounts = getPairCounts(pairName.split('-')[1:], fileLocs)
            logging.debug('Acquired file counts %s' % fileCounts)

            if len(fileCounts) > 0:
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
                        logging.info('Update of page {0} succeeded ({1}{0})'.format(pageTitle, 'http://wiki.apertium.org/wiki/')) 
                    else:
                        logging.error('Update of page %s failed: %s' % (pageTitle, editResult))
                else:
                    pageContents = createStatsSection(fileCounts, requester=args.requester)
                    pageContents = addCategory(pageContents)

                    editResult = editPage(pageTitle, pageContents, editToken)
                    if editResult['edit']['result'] == 'Success':
                        logging.info('Creation of page {0} succeeded ({1}{0})'.format(pageTitle, 'http://wiki.apertium.org/wiki/'))
                    else:
                        logging.error('Creation of page %s failed: %s' % (pageTitle, editResult.text))
            else:
                logging.error('No file counts available for %s, skipping.' % repr(langs))
        else:
            logging.error('No files found for %s, skipping.' % repr(langs))
    else:
        logging.info('Not updating %s stats page' % pairName)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Create dix tables for the Apertium wiki")
    parser.add_argument('languages', nargs='+', help='list of primary languages')
    parser.add_argument('-c','--createStatsPages', help='create any missing stats pages on the Wiki', action='store_true', default=False)
    parser.add_argument('-u', '--updateStatsPages', help='update stats pages even if present on the Wiki', action='store_true', default=False)
    parser.add_argument('-l', '--loginName', help='bot login name (required if --createStatsPages present)')
    parser.add_argument('-p', '--password', help='bot password (required if --createStatsPages present)')
    parser.add_argument('-r', '--requester', help="user who requests update", default=None)
    parser.add_argument('-v', '--verbose', help="verbose mode (debug)", action='store_true', default=False)
    args = parser.parse_args()

    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)
    else:
        logging.basicConfig(level=logging.INFO)

    if args.createStatsPages and not (args.loginName and args.password):
        parser.error('--login and --password required with --createStatsPages')

    if args.createStatsPages or args.updateStatsPages:
        from bot import login, getPage, editPage, getToken, updatePairStatsSection, getPairCounts, addCategory, createStatsSection, getFileLocs
        authToken = login(args.loginName, args.password)
        moveToken = getToken('move', 'info')
        editToken = getToken('edit', 'info|revisions')
        if not all([authToken, moveToken, editToken]):
            logging.critical('Failed to obtain required token')
            sys.exit(-1)

    primaryPairs, secondaryPairs = {}, {}

    inputLangs = list(filter(lambda x: len(x) <=3, args.languages))
    inputPages = list(filter(lambda x: len(x) > 3, args.languages))

    for inputPage in inputPages:
        from bot import getPage
        logging.info('Getting wiki page %s' % inputPage)
        pageContent = getPage(inputPage)

        if pageContent:
            dixTable = re.search(r'class="[^"]*dixtable[^"]*"', pageContent)
            if dixTable:
                headersStart = pageContent.index('!!', dixTable.start())
                headersEnd = pageContent.index('\n', headersStart)
                langs = list(filter(lambda x: len(x) > 0, list(map(str.strip, pageContent[headersStart:headersEnd].split('!!')))))
                inputLangs += langs
                args.languages += langs
                logging.info('Found %s at %s page' % (langs, repr(inputPage)))
            else:
                logging.error('Unable to find dix table (labeled with class "dixtable") in %s page' % inputPage)
        else:
            logging.error('Page %s does not exist, proceeding without it' % inputPage)
    
    languages = set(comprehensiveList(inputLangs))
    inputLangs = list(collections.OrderedDict.fromkeys(inputLangs))
    args.languages = list(collections.OrderedDict.fromkeys(args.languages))
    logging.info('Using languages {}'.format(languages))

    dirs = [
        ('incubator', r'<name>(apertium-(\w{2,3})-(\w{2,3}))</name>'),
        ('nursery', r'<name>(apertium-(\w{2,3})-(\w{2,3}))</name>'),
        ('staging', r'<name>(apertium-(\w{2,3})-(\w{2,3}))</name>'),
        ('trunk', r'<name>(apertium-(\w{2,3})-(\w{2,3}))</name>'),
    ]

    for (dirPath, dirRegex) in dirs:
        svnData = str(subprocess.check_output('svn list --xml https://svn.code.sf.net/p/apertium/svn/%s/' % dirPath, stderr=subprocess.STDOUT, shell=True), 'utf-8')
        for langDir in re.findall(dirRegex, svnData, re.DOTALL):
            if set(langDir[1:]) <= languages:
                primaryPairs[frozenset({langDir[1], langDir[2]})] = (dirPath, langDir[0])
            elif langDir[1] in languages or langDir[2] in languages:
                secondaryPairs[frozenset({langDir[1], langDir[2]})] = (dirPath, langDir[0])

    svnData = str(subprocess.check_output('svn list --xml https://svn.code.sf.net/p/apertium/svn/incubator/', stderr=subprocess.STDOUT, shell=True), 'utf-8')
    for dixFileElem in etree.fromstring(svnData).findall('.//name'):
        dixFileName = dixFileElem.text
        if re.match(r'apertium-[^\.]+\.[^\.]+\.dix', dixFileName):
            try:
                pair = dixFileName.split('.')[1].split('-')
                dixLink = 'https://svn.code.sf.net/p/apertium/svn/incubator/%s' % dixFileName
                if len(pair) > 1 and pair[0] in languages or pair[1] in languages:
                    if frozenset(pair) not in primaryPairs and frozenset(pair) not in secondaryPairs:
                        if pair[0] in languages and pair[1] in languages:
                            primaryPairs[frozenset(pair)] = ('incubator', dixFileName.split('.')[0])
                        else:
                            secondaryPairs[frozenset(pair)] = ('incubator', dixFileName.split('.')[0])
                    else:
                        logging.error('%s already recorded! Skipping!' % str(pair))
            except IndexError:
                pass

    #print(primaryPairs, secondaryPairs)

    pairFormatting = {'trunk': "'''", 'incubator': "''", 'nursery': "", 'staging': "'''''"}

    dixTable = '''{| style="text-align: center;" class="wikitable dixtable"\n|- style="background: #ececec"\n! '''
    dixTable += ' '.join(['!! %s' % iso639_3Code(lang) for lang in args.languages])
    for lang1 in args.languages:
        dixTableRow = "\n|-\n| '''%s''' || " % iso639_3Code(lang1)
        dixTableCells = []
        for lang2 in args.languages:
            if lang1 == lang2:
                dixTableCells.append('-')
            else:
                possiblePairs = list(map(frozenset, itertools.product(iso639Code(lang1), iso639Code(lang2))))
                pairExists = False
                for possiblePair in possiblePairs:
                    if possiblePair in primaryPairs and not pairExists:
                        pairInfo = primaryPairs[possiblePair]

                        if args.createStatsPages or args.updateStatsPages:
                            updateOrCreateStatsPage(pairInfo[0], pairInfo[1], args.updateStatsPages, editToken)

                        strStems = '{{{{#lst:Apertium-{0}-{1}/stats|{0}-{1}_stems}}}}'.format(*tuple(pairInfo[1].split('-')[1:]))
                        formatting = pairFormatting[pairInfo[0]]
                        strPair = formatting + '[[Apertium-{0}-{1}|{0}-{1}]]'.format(*tuple(pairInfo[1].split('-')[1:])) + formatting
                        strStems = formatting + strStems + formatting if pairInfo[0] == 'trunk' else strStems
                        dixTableCells.append(strPair + '<br>' + strStems)
                        pairExists = True
                if not pairExists:
                    dixTableCells.append('')

        dixTableRow += ' || '.join(dixTableCells)
        dixTable += dixTableRow

    dixTable += '\n|-\n| ' + ' || ' * len(args.languages)

    secondaryLangs = set()
    for secondaryPair in secondaryPairs:
        if sorted(list(secondaryPair))[0] not in languages:
            secondaryLangs.add(iso639_3Code(sorted(list(secondaryPair))[0]))
        if sorted(list(secondaryPair))[1] not in languages:
            secondaryLangs.add(iso639_3Code(sorted(list(secondaryPair))[1]))

    for secondaryLang in sorted(secondaryLangs):
        dixTableRow = "\n|-\n| '''%s''' || " % iso639_3Code(secondaryLang)
        dixTableCells = []
        for primaryLang in args.languages:
            possiblePairs = list(map(frozenset, itertools.product(iso639Code(primaryLang), iso639Code(secondaryLang))))
            pairExists = False
            for possiblePair in possiblePairs:
                if possiblePair in secondaryPairs and not pairExists:
                    pairInfo = secondaryPairs[possiblePair]

                    if args.createStatsPages or args.updateStatsPages:
                        updateOrCreateStatsPage(pairInfo[0], pairInfo[1], args.updateStatsPages, editToken)

                    strStems = '{{{{#lst:Apertium-{0}-{1}/stats|{0}-{1}_stems}}}}'.format(*tuple(pairInfo[1].split('-')[1:]))
                    formatting = pairFormatting[pairInfo[0]]
                    strPair = formatting + '[[Apertium-{0}-{1}|{0}-{1}]]'.format(*tuple(pairInfo[1].split('-')[1:])) + formatting
                    strStems = formatting + strStems + formatting if pairInfo[0] == 'trunk' else strStems
                    dixTableCells.append(strPair + '<br>' + strStems)
                    pairExists = True
            if not pairExists:
                dixTableCells.append('')

        dixTableRow += ' || '.join(dixTableCells)
        dixTable += dixTableRow

    dixTable += '\n|}'
    print(dixTable)
