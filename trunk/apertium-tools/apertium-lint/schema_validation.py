from lxml import etree
import io
import sys

fileType = sys.argv[1]
dtdFile = sys.argv[2]
file = sys.argv[3]

if fileType == 'dtd':
    f = open(dtdFile, 'r')
    x = io.StringIO(f.read())
    dtd = etree.DTD(x)

    root = open(file, 'r').read()
    strRoot = str(root)
    byteRoot = bytes(strRoot, 'utf-8')
    final = etree.XML(byteRoot)

    print(dtd.assertValid(final))

elif fileType == 'xsd':
    root = open(dtdFile, 'r')
    xsd = etree.parse(root)
    xmlSchema = etree.XMLSchema(xsd)

    doc = etree.parse(file)
    print(xmlSchema.assertValid(doc))
