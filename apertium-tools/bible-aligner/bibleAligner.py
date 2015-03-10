#!/usr/bin/env python3

import argparse, os, lxml.etree, lxml.builder, re, collections, mimetypes, bz2, logging, json, sys

def validFile(fname):
    if not os.path.isfile(fname):
        raise argparse.ArgumentError('File %s does not exist!' % fname)
    return fname

numeral_map = tuple(zip(
    (1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1),
    ('M', 'CM', 'D', 'CD', 'C', 'XC', 'L', 'XL', 'X', 'IX', 'V', 'IV', 'I')
))

def roman_to_int(n):
    i = result = 0
    for integer, numeral in numeral_map:
        while n[i:i + len(numeral)] == numeral:
            result += integer
            i += len(numeral)
    return result

romanRegex = re.compile(r'\b(M{0,3}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3}))\b')
def sectionMatch(a, b):
    a = romanRegex.sub(lambda x: str(roman_to_int(x.group(1))) if x.group(1) else '', a).replace('nd', '').replace('st', '').replace('th', '')
    b = romanRegex.sub(lambda x: str(roman_to_int(x.group(1))) if x.group(1) else '', b).replace('nd', '').replace('st', '').replace('th', '')
    return a == b

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Generate TMX file that aligns verses from two bibles (bible2 aligned to bible1)')
    parser.add_argument('bible1', help='Path to first bible (txt or bz2)', type=validFile)
    parser.add_argument('bible2', help='Path to second bible (txt or bz2)', type=validFile)
    parser.add_argument('-n', '--names', help='Path to section names mapping {language: {englishName: nativeName, ...} ...}', type=validFile, default='sectionNames.json')
    parser.add_argument('-l1', '--language1', help='Language of first bible', default=None)
    parser.add_argument('-l2', '--language2', help='Language of second bible', default=None)
    parser.add_argument('-o', '--outputFile', help='Output location for TMX file', default='aligned.tmx')
    parser.add_argument('-a', '--adminLang', help='Language of notes and properties in TMX', default='en_US')
    parser.add_argument('-A', '--appendExtraText', help='Append text following line break to previous verse', action='store_true', default=False)
    parser.add_argument('-m', '--minify', help='Minify output TMX file', action='store_true', default=False)
    args = parser.parse_args()

    logging.basicConfig(level=logging.DEBUG)

    if not args.language1:
        langName = os.path.splitext(os.path.basename(args.bible1))[0].split('.')[0]
        if 2 <= len(langName) <= 3:
            args.language1 = langName
            logging.info('Inferred first language name as %s.' % args.language1)
        else:
            args.language1 = 'foo'
            logging.error('Using first language name %s, specify one with -l1.' % args.language1)

    if not args.language2:
        langName = os.path.splitext(os.path.basename(args.bible2))[0].split('.')[0]
        if 2 <= len(langName) <= 3:
            args.language2 = langName
            logging.info('Inferred second language name as %s.' % args.language2)
        else:
            args.language2 = 'bar'
            logging.error('Using second language name %s, specify one with -l1.' % args.language2)

    headerAttribs = {
        'creationtool': 'Apertium Bible Aligner',
        'creationtoolversion': '1.0.0',
        'segtype': 'sentence',
        'o-tmf': 'UTF-8',
        'adminlang': args.adminLang,
        'scrlang': args.language1,
        'datatype': 'plaintext'
    }

    def readBible(fname):
        mimetype, encoding = mimetypes.guess_type(fname)
        if encoding == 'bzip2':
            lines = bz2.open(fname, mode='rt').readlines()
        else:
            lines = open(fname, encoding='utf-8')

        bible = collections.defaultdict(dict)
        section, justStartedSection, lastSeenVerseNum = None, False, 0
        for i, line in enumerate(lines):
            line = line.strip()
            if not line and not justStartedSection:
                section = None
                justStartedSection = False
            else:
                if section:
                    if re.match(r'^(\d+)(?:\.)?\s+(.*)', line):
                        matches = re.match(r'^(\d+)(?:\.)?\s+(.*)', line)
                        currentVerseNum = int(matches.group(1))
                        bible[section][currentVerseNum] = matches.group(2)

                        if currentVerseNum > lastSeenVerseNum + 1 and lastSeenVerseNum > 0:
                            firstExtractedNum = None
                            for verseNum in range(lastSeenVerseNum + 1, currentVerseNum):
                                verseMatches = None
                                line = bible[section][lastSeenVerseNum]
                                if str(verseNum) in line:
                                    if verseNum + 1 == currentVerseNum:
                                        verseMatches = re.findall(r'\b%s(?:\.)?\s+(.*)$' % verseNum, line)
                                    else:
                                        verseMatches = re.findall(r'\b%s(?:\.)?\s+(.*?)\s+%s' % (verseNum, verseNum + 1), line)
                                if verseMatches:
                                    bible[section][verseNum] = verseMatches[0]
                                    if not firstExtractedNum:
                                        firstExtractedNum = verseNum
                                    logging.info('Extracted verse #%s of section %s from verse #%s.' % (verseNum, section, lastSeenVerseNum))
                                else:
                                    logging.error('Unable to find verse #%s of section %s.' % (verseNum, section))
                            if firstExtractedNum:
                                beforeFirstExtractedVerse = re.findall(r'(.*?)\s+%s' % firstExtractedNum, line)
                                if beforeFirstExtractedVerse:
                                    bible[section][lastSeenVerseNum] = beforeFirstExtractedVerse[0]
                                else:
                                    logging.error('Unable to find verse #%s of section %s due to it being completely extracted.' % (verseNum, section))
                                    del bible[section][lastSeenVerseNum]
                        lastSeenVerseNum = currentVerseNum
                        justStartedSection = False
                    elif justStartedSection:
                        justStartedSection = False
                    else:
                        if args.appendExtraText and lastSeenVerseNum > 0:
                            logging.info('Appending extra text %s to previous verse %s' % (repr(line), repr(bible[section][lastSeenVerseNum])))
                            bible[section][lastSeenVerseNum] += ' ' + line
                        else:
                            logging.error('Unable to understand line #%s: %s.' % (i + 1, repr(line)))
                else:
                    section = line
                    lastSeenVerseNum = 0
                    justStartedSection = True # Allows one extra line after each section

        for section, verses in bible.items():
            for i, verse in verses.copy().items():
                firstExtractedNum = None
                verseNum = i + 1
                while True:
                    if str(verseNum) in verse:
                        lastVerseMatches = re.findall(r'\b%s(?:\.)?\s+(.*)$' % verseNum, verse)
                        middleVerseMatches = re.findall(r'\b%s(?:\.)?\s+(.*?)\s+%s' % (verseNum, verseNum + 1), verse)
                        if lastVerseMatches or middleVerseMatches:
                            bible[section][verseNum] = (lastVerseMatches + middleVerseMatches)[0]
                            if not firstExtractedNum:
                                firstExtractedNum = verseNum
                            logging.info('Extracted verse #%s of section %s from verse #%s.' % (verseNum, section, i))
                        else:
                            break
                    else:
                        break
                    verseNum += 1
                if firstExtractedNum:
                    bible[section][verseNum] = re.findall(r'(.*?)\s+%s' % firstExtractedNum, verse)[0]

        return bible

    successfulUnits, unsuccessfulUnits = 0, 0

    def getTranslationUnits():
        global successfulUnits, unsuccessfulUnits
        translationUnits = []

        with open(args.names) as f:
            names = json.load(f)
            names1 = names[args.language1]
            names1 = dict(zip(names1.values(), names1.keys()))
            names2 =names[args.language2]

        bible1 = readBible(args.bible1)
        bible2 = readBible(args.bible2)

        for sectionName, verses in bible1.items():
            if sectionName in names1 or any(map(lambda x: sectionMatch(x, sectionName), names1)):
                sectionName = list(filter(lambda x: sectionMatch(x, sectionName), names1))[0]
                engSectionName = names1[sectionName]
            elif sectionName in names2 or any(map(lambda x: sectionMatch(x, sectionName), names2)):
                sectionName = list(filter(lambda x: sectionMatch(x, sectionName), names2))[0]
                engSectionName = sectionName
            elif sectionName in bible2:
                engSectionName = sectionName
            else:
                logging.error('Unable to find %s section listed in names mapping.' % repr(sectionName))
                unsuccessfulUnits += len(verses)
                continue

            if engSectionName in names2 or engSectionName in bible2:
                if any(map(lambda x: sectionMatch(x, engSectionName), bible2.keys())):
                    otherLangSectionName = list(filter(lambda x: sectionMatch(x, engSectionName), bible2.keys()))[0]
                else:
                    otherLangSectionName = names2[engSectionName]

                for i, verse in verses.items():
                    try:
                        tu = E.tu(
                            E.tuv(
                                E.seg(verse),
                                **{'{http://www.w3.org/XML/1998/namespace}lang': args.language1}
                            ),
                            E.tuv(
                                E.seg(bible2[otherLangSectionName][i]),
                                **{'{http://www.w3.org/XML/1998/namespace}lang': args.language2}
                            ),
                            E.prop(engSectionName, type='section'),
                            E.prop(str(i), type='verse')
                        )
                        translationUnits.append(tu)
                        successfulUnits += 1
                    except KeyError:
                        logging.error('Unable to find %s verse #%s in second bible section %s, check remainder of section.' % (repr(sectionName), i, repr(otherLangSectionName)))
                        unsuccessfulUnits += 1
            else:
                logging.error('Unable to find %s section in second bible using the english name %s.' % (repr(sectionName), repr(engSectionName)))
                unsuccessfulUnits += len(verses)

        return translationUnits


    E = lxml.builder.ElementMaker()
    tmx = E.tmx(
        E.header(**headerAttribs),
        E.body(
            *getTranslationUnits()
        ),
        version='1.4'
    )

    with open(args.outputFile, 'wb') as f:
        f.write(lxml.etree.tostring(tmx, pretty_print=(not args.minify), xml_declaration=True, encoding='utf-8', doctype='<!DOCTYPE tmx SYSTEM "tmx14.dtd">'))

    print('\n')
    logging.info('Sucessfully generated %s translation units and failed to generate %s possible units: %.02f%% yield.' % (successfulUnits, unsuccessfulUnits, successfulUnits / (successfulUnits + unsuccessfulUnits) * 100))
    logging.info('TMX file %s generated by aligning %s to %s!' % (args.outputFile, args.bible2, args.bible1))

