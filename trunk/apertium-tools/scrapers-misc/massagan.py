# -*- coding: utf-8 -*-
import urllib.request
import sys
import codecs
#import xml.etree.cElementTree as ET
import xml.etree.ElementTree as ETT
import html.parser as HP
import re


print("Spider for http://massagan.com/forum.php")
print("Written by Ben Stobaugh.")
print("Prepairing to crawl...")

#Get config settings
cfile = open('cfg.txt', 'r')
config = cfile.read()
spos = config.rfind('Starting Post: ')
send = config.find('\n', spos)
spost = config[spos + len('Starting Post: '):send]
pos = config.rfind('Show HTML: ')
end = config.find('\n', pos)
dohtml = config[pos + len('Show HTML: '):end]
pos = config.rfind('Check robots.txt: ')
end = config.find('\n', pos)
rtext = config[pos + len('Check robots.txt: '):end]
pos = config.rfind('Debug mode: ')
end = config.find('\n', pos)
debug = config[pos + len('Debug mode: '):end]
pos = config.rfind('Stop at: ')
end = config.find('\n', pos)
exitn = config[pos + len('Stop at: '):end]
exitn = int(exitn)
print (spost, dohtml, rtext, exitn)
cfile.close()


#Check if there are already posts in the file
#If there are, we'll start where we left off
fi = open('data.xml', encoding='utf-8')
f = fi.read()
if f == '<forum></forum>':
    rsp = 0
else:
    sp = f.rfind('ThreadID="')
    ep = f.find('"', sp+len('ThreadID="'))
    rsp = f[sp + len('ThreadID="'):ep]
    print(rsp)
if int(spost) < int(rsp):
    spost = rsp
fi.close

ttree = ETT.parse('data.xml')
root = ttree.getroot()

count = int(spost)
url = "http://massagan.com/"
robottext= "Allow: /"

#Check for any changes in the robots.txt file.
if rtext == 'true':
    request = urllib.request.urlopen("http://massagan.com/robots.txt")
    if str(request.read).find(robottext) != -1:
        sys.exit('Permissions to crawl have changed... Stopping')

print("Preperations Finished. Starting to craw " + url + 'forum.php')
print("Press Ctrl + C to exit.")

#Make/Open file
#mfile = codecs.open('test.txt', 'a')
#mfile = open('file.txt', 'a', encoding='utf-8')

def killHTML(string):
    string = re.sub('&', '', string)
    
    global debug
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
        
        if debug == 'true':
            print('Removed ' + tag + ' from text')
    return string

def getPostInfo(html, start):
    #Get username, make it an attribute
    uplace=html.find('<B>', start)
    uplace1=html.find('</B>', uplace)
    username = html[uplace+3:uplace1-1]
    #post.set('User', username)
    if debug == 'true':
        print(username)

    #Find date posted, also make it an attribute
    dplace1=html.find('<strong>', uplace1)
    dplace2=html.find('2', dplace1)
    dplace3=html.find('<', dplace2)
    date = html[dplace2:dplace3-1]
    #post.set('Date', date)
    if debug == 'true':
        print(date)

    #Get the text of the first post
    tplace = html.find('<td>', dplace1)
    tplace2 = html.find('</td>', tplace)
    tpost = html[tplace + 4:tplace2]
    #post.text = tpost
    if debug == 'true':
        print(tpost)

    return username, date, tpost, tplace2

#Main crawl loop
while True:
    if count > exitn:
        print('Maximum thread count reached. If this is an error, please change the value in the cfg.txt')
        sys.exit('Exiting...')
    
    #Get the forum thread by using the topicid
    request = urllib.request.urlopen("http://massagan.com/forum.php?mod=viewtopic&topicid=" + str(count))
    html = str(request.read().decode('utf-8', errors='replace'))
    #print(html)
    
    #Check to see if thread exists
    if html.find("Warning: mysql_fetch_row(): supplied argument is not a valid MySQL result resource in /home/massagan2011/www/forum.php on line 210") != -1:
        print("Thread not found")
        old = count
        count = count + 1
        continue
    
    #Get title and category
    hplace = html.find('<!--- Latest posts ends here -->')
    cplace = html.find('catid=', hplace)
    cplace2 = html.find('>',cplace)
    cplace3 = html.find('<', cplace2)
    category = html[cplace2+1:cplace3]
    tplace= html.find('>', cplace3 + 7)
    tplace1= html.find('<', tplace)
    topic=html[tplace+1:tplace1]

    if topic == '' and category == '':
        print('Thread ' + str(count) + ' is blank. Moving on.')
        count = count + 1
        continue

    thread = ETT.SubElement(root, "Thread")
 
    
    #Write data to file
    #mfile.write(': ' + str(count) + '\n')
    #mfile.write('Thread Title: ' + topic + '\n')
    #mfile.write('Thread Category: ' + category +'\n')
    
    thread.set('ThreadID', str(count))
    thread.set('Title', topic)
    thread.set('Category', category)
    
    print('thread: ' + str(count))
    print('t: ' + str(topic) + ' c: ' + str(category))

    post = ETT.SubElement(thread, 'Post')
    post.set('ID', 'Featured')
    #mfile.write('\nFeatured Post:\n')
    
    #Get username, put it in the file
    uplace=html.find('<B>', tplace1)
    uplace1=html.find('</B>', uplace)
    username = html[uplace+3:uplace1-1]
    if debug == 'true':
        print(username)
    #mfile.write('Username:' + username + '\n')
    post.set('User', username)

    #Find date posted, put it in the file
    dplace1=html.find('<strong>', uplace1)
    dplace2=html.find('2', dplace1)
    dplace3=html.find('<', dplace2)
    date = html[dplace2:dplace3-1]
    if debug == 'true':
        print(date)
    #mfile.write('Date: ' + date + '\n')
    post.set('Date', date.strip())

    #Get the text of the first post
    tplace = html.find('<td>', dplace1)
    tplace2 = html.find('</td>', tplace)
    tpost = html[tplace + 4:tplace2-1]
    if debug == 'true':
        print(tpost)
    if dohtml == 'false':
        tpost = killHTML(tpost)
    #mfile.write('Text: ' + tpost + '\n \n')
    post.text = tpost

        
    #Get all start of posts, this should stay the same between pages
    pplace = html.find('<table width="100%"', tplace2)

    #Find out how many pages are in the thread
    pages = 1
    while True:
        r = pages * 25
        href = 'forum.php?mod=viewtopic&topicid='+ str(count) + '&start=' + str(r)
        if html.find(href,pplace) == -1:
            break
        else:
            pages = pages + 1

    print(pages)
    #Scan the posts on each page
    parray = range(1,pages)
    for tpage in parray:
        old = ''
        
        if parray[-1] == tpage: #If we are on the last page
            if tpage == 1: #If there is only one page. 
                pass
            else:
                href = 'forum.php?mod=viewtopic&topicid='+ str(count) + '&start=' + str(tpage)
                rq = urllib.request.urlopen(url + href)
                html = str(rq.read().decode('utf-8', errors='replace'))
   
            #Get the number of posts on the page
            tp = html.count('<tr bgcolor="#FFFFFF">')

            for tpost in range(1, tp): #Cycle through posts
                if tpost % 2 == 1:
                    if old == '':
                        user, date, apost, old = getPostInfo(html, pplace)
                        if dohtml == 'false':
                            apost = killHTML(apost)
                        xpost = ETT.SubElement(thread, 'Post')
                        xpost.set('ID', str((tpost // 2) + 1))
                        xpost.set('User', user)
                        xpost.set('Date', date.strip())
                        xpost.text = apost
                    else:
                        user, date, apost, old = getPostInfo(html, old)
                        if dohtml == 'false':
                            apost = killHTML(apost)
                        xpost = ETT.SubElement(thread, 'Post')
                        xpost.set('ID', str((tpost // 2) + 1))
                        xpost.set('User', user)
                        xpost.set('Date', date.strip())
                        xpost.text = apost
                        
                else:
                    old = html.find('</tr>', old)
                    continue
                            
                
        else:
            if tpage == 1:
                #This is the first page, no request needed.
                pass
            else:
                href = 'forum.php?mod=viewtopic&topicid='+ str(count) + '&start=' + str(tpage)
                rq = urllib.request.urlopen(url + href)
                html = str(rq.read().decode('utf-8', errors='replace'))
                
                
            for tpost in range(1, 50): #Can assume page has 25 posts
                if tpost % 2 == 1:
                    if old == '':
                        user, date, apost, old = getPostInfo(html, pplace)
                        if dohtml == 'false':
                            apost = killHTML(apost)
                        xpost = ETT.SubElement(thread, 'Post')
                        xpost.set('ID', str((tpost // 2) + 1))
                        xpost.set('User', user)
                        xpost.set('Date', date.strip())
                        xpost.text = apost
                    else:
                        user, date, apost, old = getPostInfo(html, old)
                        if dohtml == 'false':
                            apost = killHTML(apost)
                        xpost = ETT.SubElement(thread, 'Post')
                        xpost.set('ID', str((tpost // 2) + 1))
                        xpost.set('User', user)
                        xpost.set('Date', date.strip())
                        xpost.text = apost               
                else:
                    old = html.find('</tr>', old)
                    continue

    tree = ETT.ElementTree(root)
    tree.write("data.xml")
    h = HP.HTMLParser()
    fi = open('data.xml','r+', encoding='utf-8', errors='replace')
    f = fi.read()
    fi.close()
    t = h.unescape(f)
    file = open('data.xml','w', encoding='utf-8', errors='replace')
    
    if t[0:len('<?xml version="1.0" encoding="UTF-8"?>')] != '<?xml version="1.0" encoding="UTF-8"?>':
        t = '<?xml version="1.0" encoding="UTF-8"?>' + t

    file.write(t)
    file.close
    count = count + 1
