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
import urllib.request, re, time, argparse, sys
import romanclass as roman

if sys.version_info < (3, 3, 0): fileError = IOError
else: fileError = FileNotFoundError
    
parser = argparse.ArgumentParser(description = 'Scrape ibt.org')
parser.add_argument('-l', action = 'store', nargs = '*', help = 'Scrape the bibles with these codes')
parser.add_argument('-a', action = 'store_const', const = 2, help = 'List all the valid language codes')
args = parser.parse_args()
urls = args.l


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
            
            firstUrl = prefix + '&l=' + urlB
            #print(firstUrl)
            soup = BeautifulSoup(urllib.request.urlopen(firstUrl).read())
            selchap = soup.find('select', {'id':'selchap'})
            chap = [(option['value'], option.text) for option in selchap.find_all('option')]
            #print(chap)
            for urlC, fullC in chap:
                outfile.write(fullB + ' ' + str(roman.Roman(urlC)) + '\n')
                
                u = 'http://ibt.org.ru/en/text.htm?m=' + results.group(1) + '&l=' + urlB + '.' + str(urlC) + '&g=0'
                s = allPages(u, results.group(1))
                outfile.write(s + '\n')

def allPages(url, bible):
    urlparts = url.split('?')
    try:
        with open('.\\bible\\' + urlparts[1] + '.html', encoding = 'utf-8') as infile:
            text = infile.read()
    except fileError:
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


CODES = {   'ADG'   :   'Adygei',
            'AGL'   :   'Agul',
            'AVR'   :   'Avar',
            'CHV'   :   'Chuvash',
            'CTT'   :   'Crimean Tatar',
            'KHK'   :   'Khakas',
            'KJV'   :   'English',
            'WEB'   :   'English',
            'KUMYK' :   'Kumyk',
            'KYLSC' :   'Kyrgyz',
            'KYROHC':   'Kyrgyz',
            'KYLSA' :   'Kyrgyz Arabic',
            'KYROHA':   'Kyrgyz Arabic',
            'OSS'   :   'Ossetic',
            'TTR'   :   'Tatar',
            'TKL'   :   'Turkmen',
            'TKLI'  :   'Turkmen',
            'TKCI'  :   'Turkmen Cyrillic',
            'RSP'   :   'Russian',
            'UZVL'  :   'Uzbek',
            'UZIBTL':   'Uzbek',
            'UZV'   :   'Uzbek Cyrillic',
            'UZIBT' :   'Uzbek Cyrillic',
            'LXX'   :   'Greek',
            'TR'    :   'Greek',
            'OSMHB' :   'Hebrew'
}
if __name__ == '__main__':
    if args.a == 2:
        for x in sorted(CODES):
            print(x, '\t', CODES[x])
    elif urls != None:
        for url in urls:
            url = url.upper()
            if url not in CODES:
                print(url, 'is not a valid code. It will be skipped.')
            else:
                print('Will begin scraping', url)
                firstPage('http://ibt.org.ru/en/text.htm?m=' + url)
    else:
        parser.parse_args(['-h'])
        print('No argument selected.')
