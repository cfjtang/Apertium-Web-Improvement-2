#!/usr/bin/env python3

import json, collections

files = ['alt', 'aze', 'bua', 'chv', 'gag', 'kaa', 'kaz', 'kir', 'kjh', 'kum', 'tat', 'tgk', 'tyv', 'xal']
data = collections.defaultdict(list)

for fname in files:
    with open('%s.dat' % fname) as f:
        data[fname] = collections.OrderedDict(map(lambda x: tuple(map(str.strip, x.split(','))), f))

with open('sectionNames.json', 'w') as f:
    f.write(json.dumps(data, ensure_ascii=False, indent=4))

