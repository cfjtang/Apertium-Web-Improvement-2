from lxml import etree as ET

with open('text.txt', "a") as mfile: 
    parser = ET.XMLParser(recover=True)
    ttree = ET.parse('data.xml',parser=parser)
    root = ttree.getroot()
    for thread in root.findall('Page'):
        for post in thread.findall('Post'):
            if post.text == None:
                continue
            print(post.text)
            mfile.write(str(post.text) + '\n\n')
    print('Done')


