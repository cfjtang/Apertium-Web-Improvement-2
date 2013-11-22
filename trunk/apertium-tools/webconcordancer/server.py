#!/usr/bin/env python3

import sys, codecs, copy, json
from bottle.bottle import route, run, template, static_file, request, abort

@route('/:file')
def server_static(file):
    return static_file(file, root='./')

@route('/getResults', method='POST')
def getResults():
    try:
        infile = open(request.forms.get('corpus'), encoding='utf-8') #PYTHON 3
        #infile = open(request.forms.get('corpus')) #PYTHON 2
    except:
        abort(404, 'File not found :' + str(request.forms.get('corpus')))
    findstring = request.forms.getunicode('string') #getunicode required

    WINDOW = int(request.forms.get('window'))
    output = []

    for line in infile.readlines():
        loc = line.find(findstring)
        if loc <= 0:
            continue

        lgt = len(line)
        mid = lgt / 2
        text_len = len(findstring)

        start = loc - WINDOW
        end = loc + WINDOW

        if start < 0 and end > lgt:
            diff = WINDOW - loc
            output.append(('%', line[0:loc], line[loc:end]))
            
        elif start > 0 and end > lgt:
            output.append(('+', line[start:loc], line[loc:end]))
            
        elif start < 0 and end < lgt:
            diff = WINDOW - loc
            output.append(('-', line[0:loc], line[loc:end]))
            
        else:
            diff = WINDOW - loc
            output.append(('_', line[start:loc], line[loc:end]))
            
    return json.dumps(output, ensure_ascii=False)

print(sys.version)
run(host='localhost', port=8080, debug=True)
