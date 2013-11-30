#!/usr/bin/env python3

import sys, codecs, copy, json, os, re
from bottle.bottle import route, run, template, static_file, request, abort
from morphAnalysisConcordancer import analyze, getLexicalUnitsStrings, parseLexicalUnitsString, tagSearch, lemmaSearch, surfaceFormSearch, rawTextSearch, searchLines, getContext, getTextFromUnits, getTextFromUnit, getLexicalUnitString

@route('/:file')
def server_static(file):
    return static_file(file, root='./')

@route('/rawCorpusSearch', method='POST')
def rawCorpusSearch():
    try:
        infile = open(request.forms.get('corpus'), encoding='utf-8') #PYTHON 3
        #infile = open(request.forms.get('corpus')) #PYTHON 2
    except:
        abort(404, 'File not found : %s' % request.forms.get('corpus'))
    findstring = request.forms.getunicode('string') #getunicode required

    window = int(request.forms.get('window'))
    output = []

    for line in infile.readlines():
        locs = [m.span() for m in re.finditer(re.escape(findstring) if not request.forms.get('regex') == 'true' else findstring, line)]
        for (termStart, termEnd) in locs:
            lgt = len(line)
            loc = termStart
            termLength = termEnd - termStart
            start = loc - window
            end = loc + window

            if start < 0 and end > lgt:
                output.append((termLength, line[0:loc], line[loc:end]))
            elif start > 0 and end > lgt:
                output.append((termLength, line[start:loc], line[loc:end]))
            elif start < 0 and end < lgt:
                output.append((termLength, line[0:loc], line[loc:end]))
            else:
                output.append((termLength, line[start:loc], line[loc:end]))
            
    return json.dumps(output, ensure_ascii=False)

@route('/apertiumSearch', method='POST')
def apertiumSearch():
    filename = request.forms.get('corpus')
    findstring = request.forms.getunicode('string') #getunicode required
    window = int(request.forms.get('window'))
    searchType = request.forms.get('mode')
    regex = request.forms.get('regex') == 'true'
    output = []
    
    if not os.path.isfile(filename):
        abort(404, 'File not found : %s' % filename)
    else:
        try:
            analysis = analyze(filename, request.forms.get('module'), request.forms.get('pair'))
        except:
            abort(400, 'Analysis Failed: %s %s' % (request.forms.get('module'), request.forms.get('pair')))
        lexicalUnitsStrings = getLexicalUnitsStrings(analysis)
        lexicalUnits = parseLexicalUnitsString(lexicalUnitsStrings)
        
        if searchType == 'tag':
            for (index, lexicalUnit) in tagSearch(lexicalUnits, findstring, regex=regex):
                output.append((lexicalUnitsStrings[index], getContext(lexicalUnits, index, window=window)))
        elif searchType == 'lemma':
            for (index, lexicalUnit) in lemmaSearch(lexicalUnits, findstring, regex=regex):
                output.append((lexicalUnitsStrings[index], getContext(lexicalUnits, index, window=window)))
        elif searchType == 'surface':
            for (index, lexicalUnit) in surfaceFormSearch(lexicalUnits, findstring, regex=regex):
                output.append((lexicalUnitsStrings[index], getContext(lexicalUnits, index, window=window)))
        
        return json.dumps(output, ensure_ascii=False)

@route('/search', method='POST')
def search():
    filename = request.forms.get('corpus')
    query = json.loads(request.forms.getunicode('query')) #getunicode required
    
    if not os.path.isfile(filename):
        abort(404, 'File not found : %s' % filename)
    else:
        try:
            analysis = analyze(filename, request.forms.get('module'), request.forms.get('pair'))
        except:
            abort(400, 'Analysis Failed: %s %s' % (request.forms.get('module'), request.forms.get('pair')))
        lexicalUnitsStrings = getLexicalUnitsStrings(analysis, splitByLines = True)
        lexicalUnits = parseLexicalUnitsString(lexicalUnitsStrings, splitByLines = True)
        
        lexicalUnitsNotSplit = sum(lexicalUnits, [])
        lexicalUnitsStringsNotSplit = sum(lexicalUnitsStrings, [])
        
        searchFunctions = { 'Lemma': lemmaSearch, 'Tag': tagSearch, 'Surface': surfaceFormSearch, 'Raw': rawTextSearch }

        searchFilterGroups = []
        for filterGroup in query:
            searchFilterGroup = []
            for filterName, filterArgs in filterGroup.items():
                searchFilterGroup.append((searchFunctions[filterName], (filterArgs['term'], filterArgs['regex'])))
            searchFilterGroups.append(searchFilterGroup)

        output = []
        for (line, highlight) in searchLines(lexicalUnits, searchFilterGroups):
            output.append(([(getTextFromUnit(lexicalUnit), lexicalUnitsStringsNotSplit[lexicalUnitsNotSplit.index(lexicalUnit)][0]) for lexicalUnit in line], highlight))
        return json.dumps(output, ensure_ascii=False)
        #return json.dumps([([(getTextFromUnit(lexicalUnit), getLexicalUnitString(lexicalUnit)) for lexicalUnit in line] for line in lines], ensure_ascii=False)
    
print(sys.version)
run(host='localhost', port=8080, debug=True)
