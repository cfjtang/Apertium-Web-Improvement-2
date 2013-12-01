#/usr/bin/env python3
import urllib.request
import sys
import xml.etree.ElementTree as ET
import html.parser as HP
import os
from time import sleep
import http.client

##### Change to the uCoz forums that you want to scrape #####

scrape = ["http://asyl-bilim.kz/"] #One link
#scrape = ["http://asyl-bilim.kz/", "http://pedsovet.su/", "atlantis-tv.ru"] #Or many

############################################################

print('uCoz forum scaper')
print('Written by BenStobaugh')
print('Starting up...')

h = HP.HTMLParser() #Set this up. It'll be used later

#Set up XML tree
if os.path.isfile('data.xml'):
   f = open('data.xml', 'r', encoding='utf-8', errors='replace')
   if f.read() == '':
      pstart = 0
      root = ET.Element('Scraper')
   else:
      #If there is stuff in the file, find out what the last page is
      ttree = ET.parse('data.xml')
      root = ttree.getroot()
      r = root.findall('Page[last()]')[0]
      pstart = r.attrib['PageID']
   f.close()
else:
   pstart=0
   root = ET.Element('Scraper')

def ReadHTML(request):
   try:
      return str(request.read().decode('utf-8', errors='replace')) #Read the page
   except http.client.IncompleteRead:
      print('Read was unsuccessful, trying again.')
      html = ReadHTML(request)
      return html

def GetRequest(link):
   #A recursive function to check for errors
   try:
      return urllib.request.urlopen(href) #Reuest the page
   except urllib.error.HTTPError as err:
      if err.code == 503:
        print('Your IP has been banned because of the high number of requests.')
        print('The scraper is going to sleep for an hour and then will try again')
        sleep(3600)
        req = GetRequest(link)
        return req
      else:
        sys.exit('HTTP ERROR: ' + str(err.code))
   except urllib.error.URLError:
      print("A problem has occured while trying to get the page. We will try again.")
      req = GetRequest(link)
      sleep(5)
      return req
         

def KillHTML(string):
   #Run  everything through this to avoid XML errors.
   string = string.replace('\u0000', '') #Remove null characters
   string = string.replace('&', '') #Remove &
   string = string.replace('"', '') #Remove quotes
   pos = string.find('<') #remove HTML tags.
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


print("Preperations Finished. Starting to scrape")
print("Press Ctrl + C to exit.")

#Counter of what page we are on.
count = 1

for site in scrape:
   forum = site + 'forum'
   smap = site + 'sitemap-forum.xml'

   print('Currently scraping: ' + forum)
   
   #Get the site map for the forum. This contains all of the pages.
   #We'll loop through this and effectivly scrape all of the pages.
   r = urllib.request.urlopen(smap)
   sxml = str(r.read().decode('utf-8', errors='replace'))

   #Remove the namespace before parsing
   sxml = sxml.replace(' xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd"', '')
   sroot = ET.fromstring(sxml)

   #Now, we loop through the site map we got earlier.
   for url in sroot.findall('url'):
      #Check if the page has already been scraped. If it has, skip it.
      if count <= int(pstart):
         count = count + 1
         continue
      elif count == int(pstart) + 1:
         pstart = 0

      
      href = url.find('loc').text #Get the URL of the page

      request = GetRequest(href)
      html = ReadHTML(request)
      
      
      #Get the category
      c1 = html.find('&raquo; <a class="forumBar"')
      c2 = html.find('>', c1)
      c3 = html.find('</a>',c2)
      category = KillHTML(html[c2+1:c3])

      #Get the title
      t1 = html.find('&raquo; <a class="forumBarA"')
      t2 = html.find('>', t1)
      t3 = html.find('</a>',t2)
      title = KillHTML(html[t2+1:t3])

      #Let the user know what page we are scraping. Sort of as a progress bar.
      print('On page', str(count))
      
      #Get the number of posts on the page
      postnum = html.count('<table border="0" width="100%" cellspacing="1" cellpadding="2" class="postTable">')
      start = 1

      #Set up the page XML before moving to posts
      page = ET.SubElement(root, 'Page')
      page.set('Category', category)
      page.set('PageID', str(count))
      page.set('Title', title)
      page.set('Link', href)
      c = 1

      #Loop through the posts on the page   
      for tpost in range(1,postnum):
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

      #Once done with the posts, put all of the XML in a file.
      ET.ElementTree(root).write("data.xml", encoding='UTF-8', xml_declaration=True)
      fi = open('data.xml','r', encoding='utf-8', errors='replace')
      f = fi.read()
      t = h.unescape(f)
      fi = open('data.xml','w', encoding='utf-8', errors='replace')
      fi.write(t)
      fi.close()
      count = count + 1
      
print('Done..! All posts have been scraped')
