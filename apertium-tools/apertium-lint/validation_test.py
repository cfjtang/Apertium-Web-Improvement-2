from lxml import etree


def validate(xmlparser, xmlfilename):
    try:
        with open(xmlfilename, 'r') as f:
            etree.fromstring(f.read(), xmlparser)
        return True
    except etree.XMLSchemaError:
        return False


with open('dix.dtd', 'r') as f:
    schema_root = etree.XML(f.read())


schema = etree.XMLSchema(schema_root)
xmlparser = etree.XMLParser(schema=schema)

filenames = ['apertium-eo-en.en.dix']
for filename in filenames:
    if validate(xmlparser, filename):
        print('%s validates' % filename)
    else:
        print("%s doesn't validate" % filename)
