import re, pprint
import subprocess

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
    return sum([ambiguousForm[1] for ambiguousForm in lexicalUnit[0][1]], [])

def stringSearch(lexicalUnitsStrings, searchTerm):
    return [(index, lexicalUnitString) for (index, lexicalUnitString) in enumerate(lexicalUnitsStrings) if searchTerm in lexicalUnitString[0]]

def tagSearch(lexicalUnits, searchTag):
    return [(index, lexicalUnit) for (index, lexicalUnit) in enumerate(lexicalUnits) if searchTag in getTags(lexicalUnit)]

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
    
analysis = analyze('3_sentences', '/home/apertium/Desktop/apertium-en-es', 'en-es-anmor')
lexicalUnitsStrings = getLexicalUnitsStrings(analysis)
lexicalUnits = parseLexicalUnitsString(lexicalUnitsStrings)

testIndex = 8
print(lexicalUnits[testIndex])
print(getTags(lexicalUnits[testIndex]))
print(getSurfaceForm(lexicalUnits[testIndex]))
print(getLemmas(lexicalUnits[testIndex]))
print(getContext(lexicalUnits, testIndex))

pprint.pprint(tagSearch(lexicalUnits, '<p3>'))
pprint.pprint(surfaceFormSearch(lexicalUnits, 'previous'))
pprint.pprint(lemmaSearch(lexicalUnits, 'council'))