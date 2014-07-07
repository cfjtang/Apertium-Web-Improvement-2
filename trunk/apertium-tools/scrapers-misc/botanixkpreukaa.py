#!/usr/bin/python3

import sys, lxml.html

#template="http://www.botanix.kpr.eu/kaa/index.php?text=%s"
template="http://www.botanix.kpr.eu/kaa/print.php?t=%s"

for pageNum in range(int(sys.argv[1]), int(sys.argv[2]) + 1):
	doc = lxml.html.parse(template % pageNum).getroot()
	#for el in doc.find_class("text-jednotlivy"):
	for el in doc.xpath("//div"):
		print(el.text_content())
