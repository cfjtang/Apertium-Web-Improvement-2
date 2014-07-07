#!/usr/bin/python3

import sys, lxml.html

for pageNum in range(int(sys.argv[1]), int(sys.argv[2]) + 1):
    linkTags = lxml.html.parse('http://www.tatarstan.ru/tat/index.htm/news/tape/?page=%s' % pageNum).xpath("descendant-or-self::*[contains(concat(' ', normalize-space(@class), ' '), ' list-item-title ')]/descendant::a")
    links = [(linkTag.get('href'), linkTag.get('href').replace('/tat/', '/rus/')) for linkTag in linkTags]
    for linkPair in links:
        tatContent, rusContent = map(lambda l: lxml.html.parse(l).xpath("descendant-or-self::*[contains(concat(' ', normalize-space(@class), ' '), ' news ')]")[0].text_content(), linkPair)
        print(tatContent, rusContent)

