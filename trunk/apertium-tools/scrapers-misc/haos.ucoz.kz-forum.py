# -*- coding: utf-8 -*-
import urllib.request
import sys
import xml.etree.ElementTree as ET
import html.parser as HP
import fnmatch
import re
import os

print('Starting up...')

request = urllib.request.urlopen('http://asyl-bilim.kz/sitemap-forum.xml')
shtml = str(request.read().decode('utf-8', errors='replace'))
shtml = shtml.replace(' xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd"', '')

sroot = ET.fromstring(shtml)
#sroot = sxml.getroot()

count = 1
url = "http://haos.ucoz.kz/"
forum = "http://haos.ucoz.kz/forum/"
smap = "http://haos.ucoz.kz/sitemap-forum.xml"

h = HP.HTMLParser()

#Set up tree
if os.path.isfile('data.xml'):
   f = open('data.xml', 'r', encoding='utf-8', errors='replace')
   if f.read() == '':
      pstart = 0
      root = ET.Element('Forum')
   else:
      parser = ET.XMLParser(encoding='utf-8')
      ttree = ET.parse('data.xml')
      root = ttree.getroot()
      r = root.findall('Page[last()]')[0]
      pstart = r.attrib['PageID']
   f.close()
else:
   pstart=0
   root = ET.Element('Forum')
   

print("Preperations Finished. Starting to craw " + url + 'forum.php')
print("Press Ctrl + C to exit.")

def KillHTML(string):
   string = re.sub('\u0000', '', string)
   string = re.sub('&', '', string)
   string = re.sub('"', '', string)
   pos = string.find('<')
   while pos != -1:
      endp = string.find('>', pos)
      tag = string[pos:endp+1]
      string = string.replace(tag, '')
      pos = string.find('<', pos)
      if tag == '':
         string = re.sub('<', '', string)
         string = re.sub('>', '', string)
         print('KillHTML stopping...')
         break
   return string.strip()
for url in sroot.findall('url'):
   if count <= int(pstart):
      count = count + 1
      #print('lower')
      continue
   href = url.find('loc').text
   
   request = urllib.request.urlopen(href)
   html = str(request.read().decode('utf-8', errors='replace'))

   ##Get topic and category
   c1 = html.find('&raquo; <a class="forumBar"')
   c2 = html.find('>', c1)
   c3 = html.find('</a>',c2)
   category = KillHTML(html[c2+1:c3])

   t1 = html.find('&raquo; <a class="forumBarA"')
   t2 = html.find('>', t1)
   t3 = html.find('</a>',t2)
   title = KillHTML(html[t2+1:t3])

   print('On page', str(count))
   
   
   postnum = html.count('<table border="0" width="100%" cellspacing="1" cellpadding="2" class="postTable">')
   start = 1

   page = ET.SubElement(root, 'Page')
   page.set('Category', category)
   page.set('PageID', str(count))
   page.set('Title', title)
   page.set('Link', href)
   c = 1
   for post in range(1,postnum):
      #Get User
      u1 = html.find('<a class="postUser"', start)
      u2 = html.find('>', u1)
      u3 = html.find('</a>',u2)
      user = KillHTML(html[u2 + 1:u3])
      #print(user)

      #Get Date
      d1 = html.find('<td class="postTdTop">', start)
      d2 = html.find(', ', d1)
      d3 = html.find(' |', d2)
      date = KillHTML(html[d2 + 1:d3])

      #Get Post
      p1 = html.find('<span class="ucoz-forum-post"', start)
      p2 = html.find('>', p1)
      p3 = html.find('</span>', p2)
      apost = KillHTML(html[p2 + 1:p3])
      #print(apost)

      #Put 'em all in the XML tree
      post = ET.SubElement(page, 'Post')
      post.set('User', user)
      post.set('Date', date)
      post.set('PostID', str(c))
      post.text = apost
      
      start = p3 + 1
      c = c + 1

   ET.ElementTree(root).write("data.xml", encoding='UTF-8')
   fi = open('data.xml','r', encoding='utf-8', errors='replace')
   f = fi.read()
   t = h.unescape(f)
   fi = open('data.xml','w', encoding='utf-8', errors='replace')
   fi.write(t)
   fi.close()
   count = count + 1

print('Done..! All posts have been scraped')
