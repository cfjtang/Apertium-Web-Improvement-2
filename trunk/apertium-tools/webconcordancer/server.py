#!/usr/bin/env python3

import sys, codecs, copy, json, os
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
        loc = line.find(findstring)
        if loc <= 0:
            continue

        lgt = len(line)
        start = loc - window
        end = loc + window

        if start < 0 and end > lgt:
            output.append(('%', line[0:loc], line[loc:end]))
        elif start > 0 and end > lgt:
            output.append(('+', line[start:loc], line[loc:end]))
        elif start < 0 and end < lgt:
            output.append(('-', line[0:loc], line[loc:end]))
        else:
            output.append(('_', line[start:loc], line[loc:end]))
            
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
        analysis = analyze(filename, '/home/apertium/Desktop/apertium-en-es', 'en-es-anmor')
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
