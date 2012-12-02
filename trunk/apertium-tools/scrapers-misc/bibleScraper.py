#!/usr/bin/env python3

# WARNING
# ONLY USE THIS SCRIPT WITH PERMESSION FROM ibt.org.ru ADMINISTRATORS
# UNAUTHORIZED ACCESS OF ibt.org.ru IS ILLEAGL IN MOST COUNTRIES!!!
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

from bs4 import BeautifulSoup
import urllib.request, re, time, argparse
import romanclass as roman

parser = argparse.ArgumentParser(description = 'Scrape ibt.org')
parser.add_argument('codes', action = 'store', nargs = '+')
args = parser.parse_args()
urls = args.codes
print(urls)
def firstPage(url):
    
    results = re.search('m=(.*)', url)
    filename = results.group(1) + ".out"
    
    prefix = url.split('l=')[0]
    
    
    text = urllib.request.urlopen(url)
    soup = BeautifulSoup(text)
    
    selbook = soup.find('select', {'id':'selbook'})
    books = [(option['value'], option.text) for option in selbook.find_all('option')]
        
    with open(filename, 'w', encoding = 'utf-8') as outfile:
        
        for urlB, fullB in books:
            outfile.write(fullB + '\n')
            
            firstUrl = prefix + '&l=' + urlB
            print(firstUrl)
            soup = BeautifulSoup(urllib.request.urlopen(firstUrl).read())
            selchap = soup.find('select', {'id':'selchap'})
            chap = [(option['value'], option.text) for option in selchap.find_all('option')]
            print(chap)
            for urlC, fullC in chap:
                outfile.write(str(roman.Roman(urlC)) + '\n')
                
                u = 'http://ibt.org.ru/en/text.htm?m=' + results.group(1) + '&l=' + urlB + '.' + str(urlC) + '&g=0'
                s = allPages(u, results.group(1))
                outfile.write(s + '\n')

def allPages(url, bible):
    urlparts = url.split('?')
    try:
        with open('.\\bible\\' + urlparts[1] + '.html', encoding = 'utf-8') as infile:
            text = infile.read()
    except FileNotFoundError:
        text = urllib.request.urlopen(url).read().decode('utf-8')
        print("Downloaded")
        with open('.\\bible\\' + urlparts[1] + '.html', 'w', encoding = 'utf-8') as outfile:
            outfile.write(text)
        time.sleep(0.5)
    soup = BeautifulSoup(text)
    flowcolumn = soup.find('div', {'id':'flowcolumn'})
    s = ''
    i = 1
    for verse in flowcolumn.find_all('span', {'class':'cs-' + bible}):
        if verse.sup != None:
            verse.sup.clear()
        #print verse['id']
        #print verse.text.encode('utf-8')
        s += str(i)+ '.' + verse.text.strip().strip() + '\n'
        i += 1
    return s
    
for url in urls:
    firstPage('http://ibt.org.ru/en/text.htm?m=' + url)
