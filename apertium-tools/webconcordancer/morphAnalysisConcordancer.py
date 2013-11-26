import re, pprint, subprocess, argparse

def analyze(fileName, moduleDir, strPair):
    p1 = subprocess.Popen(["cat", fileName], stdout=subprocess.PIPE)
    p2 = subprocess.Popen(["apertium", "-d %s" % moduleDir, strPair], stdin = p1.stdout, stdout = subprocess.PIPE)
    p1.stdout.close()
    return p2.communicate()[0].decode('utf-8')

def getLexicalUnitsStrings(analysis):
    return re.findall(r'\^([^\$]*)\$([^\^]*)', analysis)
    
def parseLexicalUnitsString(lexicalUnitsStrings):
    lexicalUnits = []
    for (lexicalUnitString, seperator) in lexicalUnitsStrings:
        forms = lexicalUnitString.split('/')
        surfaceForm = forms[0]
        ambiguousUnitsString = forms[1:]
        
        ambiguousUnits = []
        for ambiguousUnit in ambiguousUnitsString:
            if not ambiguousUnitsString[0][0] == '*':
                splitUnit = re.findall(r'([^<]*)<([^>]*)>', ambiguousUnit)
                lemma = splitUnit[0][0]
                tags = ['<%s>' % tagGroup[1] for tagGroup in splitUnit]
                ambiguousUnits.append((lemma, tags))
        lexicalUnits.append(((surfaceForm, ambiguousUnits), seperator))
    return lexicalUnits
    
def getLexicalUnit(lexicalUnit):
    return lexicalUnit[0]

def getSeperator(lexicalUnit):
    return lexicalUnit[1]

def getSurfaceForm(lexicalUnit):
    return lexicalUnit[0][0]

def getLemmas(lexicalUnit):
    return [ambiguousForm[0] for ambiguousForm in lexicalUnit[0][1]]

def getTags(lexicalUnit):
    return set(sum([ambiguousForm[1] for ambiguousForm in lexicalUnit[0][1]], []))

def stringSearch(lexicalUnitsStrings, searchTerm):
    return [(index, lexicalUnitString) for (index, lexicalUnitString) in enumerate(lexicalUnitsStrings) if searchTerm in lexicalUnitString[0]]

def tagSearch(lexicalUnits, searchTags):
    searchTags = set(re.findall('<[^>]*>', searchTags))
    return [(index, lexicalUnit) for (index, lexicalUnit) in enumerate(lexicalUnits) if getTags(lexicalUnit).issuperset(searchTags)]

def surfaceFormSearch(lexicalUnits, searchTerm):
    return [(index, lexicalUnit) for (index, lexicalUnit) in enumerate(lexicalUnits) if searchTerm in getSurfaceForm(lexicalUnit)]

def lemmaSearch(lexicalUnits, searchTerm):
    return [(index, lexicalUnit) for (index, lexicalUnit) in enumerate(lexicalUnits) if searchTerm in getLemmas(lexicalUnit)]
    
def getContext(lexicalUnits, index, window=15):
    surfaceForm = lexicalUnits[index][0]
    start = index - window
    end = index + window
    length = len(lexicalUnits)

    if start < 0 and end > length:
        output = (''.join([getSurfaceForm(lexicalUnit) + getSeperator(lexicalUnit) for lexicalUnit in lexicalUnits[0:index]]),
                  ''.join([getSurfaceForm(lexicalUnit) + getSeperator(lexicalUnit) for lexicalUnit in lexicalUnits[index:end]]))
    elif start > 0 and end > length:
        output = (''.join([getSurfaceForm(lexicalUnit) + getSeperator(lexicalUnit) for lexicalUnit in lexicalUnits[start:index]]),
                  ''.join([getSurfaceForm(lexicalUnit) + getSeperator(lexicalUnit) for lexicalUnit in lexicalUnits[index:end]]))
    elif start < 0 and end < length:
        output = (''.join([getSurfaceForm(lexicalUnit) + getSeperator(lexicalUnit) for lexicalUnit in lexicalUnits[0:index]]),
                  ''.join([getSurfaceForm(lexicalUnit) + getSeperator(lexicalUnit) for lexicalUnit in lexicalUnits[index:end]]))
    else:
        output = (''.join([getSurfaceForm(lexicalUnit) + getSeperator(lexicalUnit) for lexicalUnit in lexicalUnits[start:index]]),
                  ''.join([getSurfaceForm(lexicalUnit) + getSeperator(lexicalUnit) for lexicalUnit in lexicalUnits[index:end]]))
    return (output[0].replace('\r\n', ' ').replace('\n', ' '), output[1].replace('\r\n', ' ').replace('\n', ' '))

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Search the morphological analysis of a corpus.')
    parser.add_argument('corpus', help='Path to corpus')
    parser.add_argument('module', help='Path to language module')
    parser.add_argument('pair', help='Name of language pair')
    parser.add_argument('string', help='Search string')
    parser.add_argument('searchType', help='Type of search to perform', choices=['tag', 'surfaceForm', 'lemma'])
    parser.add_argument('-w', '--window', help='Search window', type=int, default=10)
    args = parser.parse_args()

    analysis = analyze(args.corpus, args.module, args.pair)
    lexicalUnitsStrings = getLexicalUnitsStrings(analysis)
    lexicalUnits = parseLexicalUnitsString(lexicalUnitsStrings)
    
    searchMethods = {'tag': tagSearch, 'surfaceForm': surfaceFormSearch, 'lemma': lemmaSearch }
    searchResults = searchMethods[args.searchType](lexicalUnits, args.string)
    for (index, lexicalUnit) in searchResults:
        print(''.join(getContext(lexicalUnits, index, window=args.window)))
        print(lexicalUnitsStrings[index][0])
        print()

'''if __name__ == '__main__':
    analysis = analyze('3_sentences', '/home/apertium/Desktop/apertium-en-es', 'en-es-anmor')
    lexicalUnitsStrings = getLexicalUnitsStrings(analysis)
    lexicalUnits = parseLexicalUnitsString(lexicalUnitsStrings)

    testIndex = 8
    print(lexicalUnits[testIndex])
    print(getTags(lexicalUnits[testIndex]))
    print(getSurfaceForm(lexicalUnits[testIndex]))
    print(getLemmas(lexicalUnits[testIndex]))
    print(getContext(lexicalUnits, testIndex))

    pprint.pprint(tagSearch(lexicalUnits, '<n><sg>'))
    pprint.pprint(surfaceFormSearch(lexicalUnits, 'previous'))
    pprint.pprint(lemmaSearch(lexicalUnits, 'council'))'''
