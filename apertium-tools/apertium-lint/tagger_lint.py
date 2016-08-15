# coding=utf-8
# -*- encoding: utf-8 -*-



import json, sys, re, xml, os, hashlib, string
import subprocess
from lxml import etree as ET
from lxml.etree import tostring
from itertools import chain
from collections import OrderedDict

def readConfig():
	with open("config.json") as dataFile:
		data = json.load(dataFile)
		return data["tagger"]

def stringify_children(node):
	text = ([node.text]+list(chain(*([tostring(child).decode('utf-8')] for child in node.getchildren()))))
	return text

def getLineNumber(pattern):
	with open(fName) as curFile:
		for num, line in enumerate(curFile, 1):
			if pattern in line:
				return num

def taggerErrors(errorsConf):
	for key in errorsConf:
		if errorsConf[key]["enable"] == True:
			valid = globals().copy()
			valid.update(locals())
			method = valid.get(key)
			if not method:
				raise NotImplementedError("Method %s not implemented" % key)
			method()

def parseLabels():

	tagsetData = {}
	multTagset = {}
	taggerPath = "./tagger"
	tagsetPath = ".//tagset"

	for tagset in tree.findall(tagsetPath):
		for label in tagset.iterchildren():
			labelDict = {}
			try: labelName = label.attrib['name']
			except KeyError: labelName = None

			try: labelClosed = label.attrib['closed']
			except KeyError: labelClosed = False

			tagList = []

			for tagsItem in label.iterchildren():

				try: lemma = tagsItem.attrib['lemma']
				except KeyError: lemma = None

				try: tags = tagsItem.attrib['tags']
				except KeyError : tags = None

				tagList.append((lemma, tags))

			labelDict['name'] = labelName
			labelDict['closed'] = labelClosed
			labelDict['tagsItem'] = tagList

			tagsetData[labelName] = labelDict
			
			print (tagList)
			print(type(tagList))

def main(arg1):
	"""
	Main function, handles the lint's workflow
	"""
	global errorsConf, tree, fName

	errorsConf = readConfig()
	fName = arg1

	tree = ET.parse(fName)	

	parseLabels()
if __name__=="__main__":
		sys.exit(main(sys.argv[1]))