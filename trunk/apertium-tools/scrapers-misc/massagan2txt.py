import xml.etree.ElementTree as ET
import sys

with open('text.txt', "a", encoding="UTF-8", errors="replace") as file: 
    ttree = ET.parse('data.xml')
    root = ttree.getroot()
    for thread in root.findall('Thread'):
        for post in thread.findall('Post'):
            if post.text == None:
                continue
            print(post.text)
            file.write(str(post.text) + '\n\n')
    print('Done')
