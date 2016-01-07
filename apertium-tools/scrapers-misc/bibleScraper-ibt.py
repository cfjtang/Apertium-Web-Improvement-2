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
import urllib.request, re, time, argparse, sys, os
import romanclass as roman

if sys.version_info < (3, 3, 0): fileError = IOError
else: fileError = FileNotFoundError
    
parser = argparse.ArgumentParser(description = 'Scrape ibt.org')
parser.add_argument('-l', action = 'store', nargs = '*', help = 'Scrape the bibles with these codes')
parser.add_argument('-x', action = 'store', nargs = '*', help = 'Skip scraping certain book(s); OT to get just New Testament')
parser.add_argument('-a', action = 'store_const', const = 2, help = 'List all the valid language codes')
parser.add_argument('-s', action = 'store_const', const = 2, help = 'Parse titles within each chapter')
parser.add_argument('-q', action = 'store_false', help = 'Suppress progress messages')
parser.add_argument('-u', action = 'store_true', help = 'Add to file, don\'t overwrite')
args = parser.parse_args()
urls = args.l
if args.x:
    toSkip = args.x
    OT = ['Genesis', 'Exodus', 'Leviticus', 'Numbers', 'Deuteronomy', 'Joshua', 'Judges', 'Ruth', '1 Samuel', '2 Samuel', '1 Kings', '2 Kings', '1 Chronicles', '2 Chronicles', 'Ezra', 'Nehemiah', 'Esther', 'Job', 'Psalms', 'Proverbs', 'Ecclesiastes', 'Song of Songs', 'Isaiah', 'Jeremiah', 'Lamentations', 'Ezekiel', 'Daniel', 'Hosea', 'Joel', 'Amos', 'Obadiah', 'Jonah', 'Micah', 'Nahum', 'Habakkuk', 'Zechariah', 'Zephaniah', 'Haggai', 'Malachi']
    if "OT" in args.x:
        toSkip = OT
else:
	toSkip = []

def firstPage(url):
    
    results = re.search('m=(.*)', url)
    filename = results.group(1) + ".out"
    
    prefix = url.split('l=')[0]
    
    
    text = urllib.request.urlopen(url)
    soup = BeautifulSoup(text)
    
    selbook = soup.find('select', {'id':'selbook'})
    books = [(option['value'], option.text) for option in selbook.find_all('option')]

    if args.u:
        mode = 'a'
    else:
        mode = 'w'
    with open(filename, mode, encoding = 'utf-8') as outfile:
        if not os.path.isdir('.cache'): os.mkdir('.cache')
            
        for urlB, fullB in books:
            print(fullB, end='')
            if fullB in toSkip:
                print(" [skipping]")
            else:
                sys.stdout.flush()
                firstUrl = prefix + '&l=' + urlB
                #print(firstUrl)
                soup = BeautifulSoup(urllib.request.urlopen(firstUrl).read())
                selchap = soup.find('select', {'id':'selchap'})
                chap = [(option['value'], option.text) for option in selchap.find_all('option')]
                print(": ", end='')
                for urlC, fullC in chap:
                    outfile.write(fullB + ' ' + str(roman.Roman(urlC)) + '\n')
                    print(fullC, end='')
                    sys.stdout.flush()
                    u = 'http://ibt.org.ru/en/text.htm?m=' + results.group(1) + '&l=' + urlB + '.' + str(urlC) + '&g=0'
                    s = allPages(u, results.group(1))
                    print(".", end='')
                    sys.stdout.flush()
                    outfile.write(s + '\n')
                    print(" ", end='')
                    sys.stdout.flush()
                print()

def allPages(url, bible):
    urlparts = url.split('?')
    
    filepath = os.path.join(os.path.curdir, '.cache', urlparts[1]+'.html') 
    try:
        with open(filepath, encoding = 'utf-8') as infile:
            text = infile.read()
    except fileError:
        text = urllib.request.urlopen(url).read().decode('utf-8')
        #print("Downloaded")
        with open(filepath, 'w', encoding = 'utf-8') as outfile:
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
        if verse.previous_sibling != None:
            try:
                if verse.previous_sibling.name == 'div' and args.s == 2:
                    s += verse.previous_sibling.text.strip() + '\n'
            except AttributeError:
                # Was a string/skip
                pass
            
        s += str(i)+ '. ' + verse.text.strip().strip() + '\n'
        i += 1
    return s


CODES = {   'ADG'   :   'Adygei',
            'AGL'   :   'Agul',
            'AVR'   :   'Avar',
            'CHV'   :   'Chuvash',
            'CRT'   :   'Crimean Tatar',
            'KHK'   :   'Khakas',
            'XKS'   :   'Khakas',
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
            'TYV'   :   'Tuvan',
            'TVN'   :   'Tuvan',
            'RSP'   :   'Russian',
            'UZVL'  :   'Uzbek',
            'UZIBTL':   'Uzbek',
            'UZV'   :   'Uzbek Cyrillic',
            'UZIBT' :   'Uzbek Cyrillic',
            'LXX'   :   'Greek',
            'TR'    :   'Greek',
            'OSMHB' :   'Hebrew',
            'KRK'   :   'Qaraqalpaq Latin',
            'KRKL'  :   'Qaraqalpaq Cyrillic',
            'SHR'   :   'Shor',
            'BUR'   :   'Buryat',
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
