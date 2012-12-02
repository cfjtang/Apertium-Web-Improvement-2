from bs4 import BeautifulSoup
import urllib, re, time
import romanclass as roman

urls = ['UZV']

def firstPage(url):
    
    results = re.search('m=(.*)', url)
    filename = results.group(1) + ".out"
    
    prefix = url.split('l=')[0]
    
    
    text = urllib.urlopen(url)
    soup = BeautifulSoup(text)
    
    selbook = soup.find('select', {'id':'selbook'})
    books = [(option['value'], option.text) for option in selbook.find_all('option')]
        
    with open(filename, 'w') as outfile:
        
        for urlB, fullB in books:
            outfile.write(fullB + '\n')
            
            firstUrl = prefix + '&l=' + urlB
            print(firstUrl)
            soup = BeautifulSoup(urllib.urlopen(firstUrl).read())
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
        with open('.\\bible\\' + urlparts[1] + '.html') as infile:
            text = infile.read()
    except IOError:
        text = urllib.urlopen(url).read()
        print("Downloaded")
        with open('.\\bible\\' + urlparts[1] + '.html', 'w') as outfile:
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
        s += str(i)+ '.' + verse.text.strip().encode('utf-8').strip() + '\n'
        i += 1
    return s
    
for url in urls:
    firstPage('http://ibt.org.ru/en/text.htm?m=' + url)
