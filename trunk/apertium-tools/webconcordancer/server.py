#!/usr/bin/env python3

import sys, codecs, copy, json, os, re
from bottle.bottle import route, run, template, static_file, request, abort
from morphAnalysisConcordancer import analyze, getLexicalUnitsStrings, parseLexicalUnitsString, tagSearch, lemmaSearch, surfaceFormSearch, getContext

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
            for (index, lexicalUnit) in tagSearch(lexicalUnits, findstring):
                output.append((lexicalUnitsStrings[index], getContext(lexicalUnits, index, window=window)))
        elif searchType == 'lemma':
            for (index, lexicalUnit) in lemmaSearch(lexicalUnits, findstring):
                output.append((lexicalUnitsStrings[index], getContext(lexicalUnits, index, window=window)))
        elif searchType == 'surface':
            for (index, lexicalUnit) in surfaceFormSearch(lexicalUnits, findstring):
                output.append((lexicalUnitsStrings[index], getContext(lexicalUnits, index, window=window)))
        
        return json.dumps(output, ensure_ascii=False)
    
print(sys.version)
run(host='localhost', port=8080, debug=True)