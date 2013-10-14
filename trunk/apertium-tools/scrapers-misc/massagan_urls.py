#!/usr/bin/env python3

from bs4 import BeautifulSoup
#import urllib.request
import http.client
import sys
import re
import time

urls = {'base': 'massagan.com',
		'forum': '/forum.php',
		'category': '/forum.php?mod=topics&catid=%s',
		'topic': '/forum.php?mod=viewtopic&topicid=%s',
	}

conn = http.client.HTTPConnection(urls['base'])

def resetConnection():
	global conn
	conn.close()
	conn = http.client.HTTPConnection(urls['base'])
	return conn

def getForum():
	global urls, conn
	ids = []

	sys.stdout.write("getting forum list.")
	sys.stdout.flush()

	forumUrl = urls['forum']
	conn.request("GET", forumUrl)
	res = conn.getresponse()
	soup = BeautifulSoup(res)
	sys.stdout.write(".")
	sys.stdout.flush()

	catid = re.compile("forum\.php\?mod\=topics\&catid\=([0-9]+)")
	allUrls = soup.findAll('a', href=catid)
	for url in allUrls:
		id = catid.match(url['href']).group(1)
		ids.append(id)
	#sys.stdout.write(str(ids))
	sys.stdout.write(".")
	sys.stdout.flush()
	return ids

def getCategory(catId):
	global urls, conn
	ids = []
	outUrls = []

	sys.stdout.write("getting category(%s) list." % catId)
	sys.stdout.flush()

	catUrl = (urls['category'] % catId)
	#sys.stdout.write(catUrl)
	sys.stdout.flush()
	conn.request("GET", catUrl)
	res = conn.getresponse()

	while res.status == 403:
		sys.stdout.write(".")
		sys.stdout.flush()
		#time.sleep(30)
		conn = resetConnection()
		conn.request("GET", catUrl)
		res = conn.getresponse()

	soup = BeautifulSoup(res)
	sys.stdout.write(".")
	sys.stdout.flush()

	catid = re.compile("forum\.php\?mod\=viewtopic\&topicid\=([0-9]+)$")
	allUrls = soup.findAll('a', href=catid)
	for url in allUrls:
		id = catid.match(url['href']).group(1)
		ids.append(id)
		theseUrls = {url['href']}
		counters = url.findParent('tr').findAll(text=re.compile("^Бетке бар")) #align="right") #, text=re.compile("^Бетке бар")) #, align="right")		#, text=re.compile("Бетке бар"))
		for counter in counters:
			blahs = counter.parent.findAll('a')
			#print(blahs)
			for blah in blahs:
				thisUrl = blah['href']
				#print(thisUrl)
				theseUrls.add(thisUrl)
		#print(theseUrls)
		outUrls+= theseUrls
	#print(outUrls)


	#sys.stdout.write(str(ids))
	sys.stdout.write(".")
	sys.stdout.flush()
	return outUrls
	#return ids

if __name__ == '__main__':
	categoryIds = set(getForum())
	sys.stdout.write("\n")
	sys.stdout.flush()

	masterUrls = set()
	#print(categoryIds)
	for categoryId in categoryIds:
		topicUrls = getCategory(categoryId)
		masterUrls = masterUrls | set(topicUrls)
		sys.stdout.write("\n")
		sys.stdout.flush()
		print(len(masterUrls))
	
	conn.close()

	with open('massagan_urls.txt', 'w') as outFile:
		for url in masterUrls:
			outFile.write(url+"\n")

