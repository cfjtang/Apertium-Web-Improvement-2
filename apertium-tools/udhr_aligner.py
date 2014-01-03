#!/usr/bin/env python3

import argparse, urllib.request, lxml, random, os, sys
from lxml import etree
from datetime import datetime

def getTree(lang):
    try:
        link = baseLink % lang
        xml = str((urllib.request.urlopen(link)).read(), 'utf-8').replace(' encoding="UTF-8"', '').replace(' xmlns="http://www.unhchr.ch/udhr"', '')
        randomFileName = "%0.12d.tmp" % random.randint(100000000000, 999999999999)
        with open(randomFileName, 'w') as f:
            f.write(xml)
        tree = etree.parse(randomFileName)
        os.remove(randomFileName)
        return tree
    except urllib.error.HTTPError:
        print('Unable to locate XML for %s through www.unicode.org' % lang)
        sys.exit(-1)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="UDHR Sentence Aligner (outputs TMX files)")
    parser.add_argument('sourceLang', help='source language')
    parser.add_argument('targetLang', help='target language')
    parser.add_argument('-o','--outFile', help='file path to save output TMX file (default = e.g. UDHRAligned.eng-spa.tmx)', default=None)
    args = parser.parse_args()

    XMLNameSpace = 'http://www.w3.org/XML/1998/namespace'

    baseLink = 'http://www.unicode.org/udhr/d/udhr_%s.xml'
    sourceTree, targetTree = getTree(args.sourceLang), getTree(args.targetLang)

    tmx = etree.Element('tmx')
    tmx.set('version', '1.4')
    header = etree.SubElement(tmx, 'header')
    header.set('creationtool', 'Apertium UDHR Aligner')
    header.set('creationtoolversion', '1.0')
    header.set('segtype', 'paragraph')
    header.set('adminlang', 'eng')
    header.set('srclang', args.sourceLang)
    header.set('datatype', 'plaintext')
    header.set('o-tmf', 'xml')
    body = etree.SubElement(tmx, 'body')

    preambleParaXPATH = 'descendant-or-self::preamble/descendant::para'

    if len(sourceTree.xpath(preambleParaXPATH)) == len(targetTree.xpath(preambleParaXPATH)):
        for paraNum, (sourcePara, targetPara) in enumerate(zip(sourceTree.xpath(preambleParaXPATH), targetTree.xpath(preambleParaXPATH))):
            tu = etree.SubElement(body, 'tu')
            tu.set('tuid', 'preamble%s' % (paraNum + 1))
            tu.set('segtype', 'paragraph')
            sectionProp = etree.SubElement(tu, 'prop')
            sectionProp.set('type', 'section')
            sectionProp.set('{%s}lang' % XMLNameSpace, 'eng')
            sectionProp.text = 'preamble'
            numProp = etree.SubElement(tu, 'prop')
            numProp.set('type', 'preambleParaNum')
            numProp.text = str(paraNum + 1)

            sourceTuv = etree.SubElement(tu, 'tuv')
            sourceTuv.set('{%s}lang' % XMLNameSpace, args.sourceLang)
            sourceSeg = etree.SubElement(sourceTuv, 'seg')
            sourceSeg.text = sourcePara.text

            targetTuv = etree.SubElement(tu, 'tuv')
            targetTuv.set('{%s}lang' % XMLNameSpace, args.targetLang)
            targetSeg = etree.SubElement(targetTuv, 'seg')
            targetSeg.text = targetPara.text
    else:
        tu = etree.SubElement(body, 'tu')
        tu.set('tuid', 'preamble')
        tu.set('segtype', 'block')
        prop = etree.SubElement(tu, 'prop')
        prop.set('type', 'section')
        prop.set('{%s}lang' % XMLNameSpace, 'eng')
        prop.text = 'preamble'

        sourceTuv = etree.SubElement(tu, 'tuv')
        sourceTuv.set('{%s}lang' % XMLNameSpace, args.sourceLang)
        sourceSeg = etree.SubElement(sourceTuv, 'seg')
        sourceSeg.text = '\n'.join(map(lambda _: _.text, sourceTree.xpath(preambleParaXPATH)))

        targetTuv = etree.SubElement(tu, 'tuv')
        targetTuv.set('{%s}lang' % XMLNameSpace, args.targetLang)
        targetSeg = etree.SubElement(targetTuv, 'seg')
        targetSeg.text = '\n'.join(map(lambda _: _.text, targetTree.xpath(preambleParaXPATH)))

    articlesXPATH = 'descendant-or-self::article'
    for articleNum, (sourceArticle, targetArticle) in enumerate(zip(sourceTree.xpath(articlesXPATH), targetTree.xpath(articlesXPATH))):
        paraXPATH = 'descendant-or-self::para'
        for sourcePara, targetPara in zip(sourceArticle.xpath(paraXPATH), targetArticle.xpath(paraXPATH)):
            tu = etree.SubElement(body, 'tu')
            tu.set('tuid', 'article%s' % (articleNum + 1))
            sectionProp = etree.SubElement(tu, 'prop')
            sectionProp.set('type', 'section')
            sectionProp.set('{%s}lang' % XMLNameSpace, 'eng')
            sectionProp.text = 'article'
            numProp = etree.SubElement(tu, 'prop')
            numProp.set('type', 'articleNum')
            numProp.text = str(articleNum + 1)

            sourceTuv = etree.SubElement(tu, 'tuv')
            sourceTuv.set('{%s}lang' % XMLNameSpace, args.sourceLang)
            sourceSeg = etree.SubElement(sourceTuv, 'seg')
            sourceSeg.text = sourcePara.text

            targetTuv = etree.SubElement(tu, 'tuv')
            targetTuv.set('{%s}lang' % XMLNameSpace, args.targetLang)
            targetSeg = etree.SubElement(targetTuv, 'seg')
            targetSeg.text = targetPara.text

    if args.outFile is None:
        args.outFile = 'UDHRAligned.%s-%s.tmx' % (args.sourceLang, args.targetLang)

    with open(args.outFile, 'wb') as f:
        f.write(etree.tostring(tmx, pretty_print=True, xml_declaration=True, encoding='UTF-8'))
        print('Output saved to %s' % args.outFile)