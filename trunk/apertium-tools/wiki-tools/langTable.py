#!/usr/bin/env python3

import argparse, re, urllib.request, sys
from lxml import etree, html
from collections import defaultdict

#http://www.unesco.org/culture/languages-atlas/index.php

def performReplacements(data):
    replacements = {
        'Altai': 'Altay',
        'Iran (Islamic Republic of)': 'Iran',
        'Khalaj, Turkic': 'Khalaj',
        'Yugur, West': 'Yugur (Western/Saryg)',
        'Khorasani Turkish': 'Khorasani',
        'Nogai': 'Nogay',
        'Azerbaijani, South': 'Azerbaijani, Southern',
        'Azerbaijani, North': 'Azerbaijani, Northern',
        'Karagas': 'Tofa',
        'The former Yugoslav Republic of Macedonia': 'Macedonia'
    }
    for key, value in data.items():
        if isinstance(value, str):
            newValue = value
            for original, new in replacements.items():
                if original in newValue:
                    newValue = newValue.replace(original, new)
            data[key] = newValue
    return data

def shortenPopulation(n):
    if isinstance(n, str) and n.count(',') and n.replace(',', '').strip().isdigit():
        n = int(n.replace(',', '').strip())
    elif isinstance(n, str) and n.strip().isdigit():
        n = int(n)
    else:
        return n

    if n < 1000:
        return '{:,}'.format(n)
    elif n < 1000000:
        return '{}K'.format(str(round(n/1000, 1)).rstrip('0').rstrip('.'))
    elif n < 1000000000:
        return '{}M'.format(str(round(n/1000000, 1)).rstrip('0').rstrip('.'))

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Create Language vulnerability table")
    parser.add_argument('dataSet', help='path to UNESCO XML data set')
    parser.add_argument('languages', nargs='+', help='list of language codes for table')
    parser.add_argument('-e','--extended', help='use extended data set', action='store_true')
    args = parser.parse_args()

    try:
        tree = etree.parse(args.dataSet)
    except:
        print('Unable to get XML data set: %s' % args.dataSet)
        sys.exit(-1)

    vulnerabilies = {"Extinct": 5, "Critically endangered": 4, "Severely endangered": 3, "Definitely endangered": 2, "Vulnerable": 1, "Safe": 0}
    languageData = []

    if not args.extended:
        table = '''{| class="wikitable sortable"
                    !rowspan=2| Language
                    !rowspan=2| ISO639-3
                    !rowspan=2| Location
                    !rowspan=2| Speakers
                    !colspan=2|Status
                    |-class="sortbottom"
                    ! Ethnologue
                    ! UNESCO'''.replace('                    ', '')

        for language in args.languages:
            data = defaultdict(lambda: '-')
            found = False

            tree = etree.parse(args.dataSet)
            codeElement = tree.xpath("//ISO639-3_codes[text()='%s']" % language)
            if codeElement:
                record = codeElement[0].getparent()
                data['name'] = record.xpath('Name_in_English')[0].text
                data['code'] = record.xpath('ISO639-3_codes')[0].text
                data['UNESCOLoc'] = record.xpath('Countries')[0].text
                vulnerability = record.xpath('Degree_of_endangerment')[0].text
                vulnerabilityNum = vulnerabilies[vulnerability]
                data['vulnerabilityNum'] = vulnerabilityNum
                data['vulnerability'] = '%s (%s)' % (vulnerabilityNum, vulnerability)
                found = True
                print('Located language "%s" in UNESCO data set' % language)
            else:
                print('Unable to locate language "%s" in UNESCO data set' % language)

            link = 'http://www.ethnologue.com/language/%s' % language
            tree = html.parse(link).getroot()
            if tree.find_class('field-name-a-language-of'):
                if 'macrolanguage' in tree.find_class('field-name-a-language-of')[0].find('div/div/h2').text:
                    raise NotImplementedError
                else:
                    data['name'] = tree.get_element_by_id('page-title').text
                    code = tree.find_class('field-name-language-iso-link-to-sil-org')[0].find('div/div/a').text
                    data['code'] = '[{} {}]'.format(link, code)
                    if tree.find_class('field-name-field-population'):
                        population = tree.find_class('field-name-field-population')[0].find('div/div/p').text
                        totalPopulation = re.findall(r'Population total all countries: ([,0-9]+).', population)
                        ethnicPopulation = re.findall(r'Ethnic population: ([,0-9]+).', population)
                        firstPopulation = re.findall(r'^[,0-9]+', population)
                        if 'no remaining speakers' in population.lower() or 'no known l1 speakers' in population.lower():
                            data['population'] = 0
                        elif 'population unknown' in population.lower():
                            data['population'] = '?'
                        elif totalPopulation:
                            data['population'] = totalPopulation[0].strip()
                        elif firstPopulation:
                            data['population'] = firstPopulation[0].strip()
                        elif ethnicPopulation:
                            data['population'] = ethnicPopulation[0].strip()
                        else:
                            data['population'] = population
                    else:
                        data['population'] = '?'
                    data['status'] = tree.find_class('field-name-language-status')[0].find('div/div/p').text.split('.')[0]
                    vulnerabilityNum = int(re.findall(r'\d+', data['status'])[0])
                    vulnerabilityLetter = re.findall('^[0-9]+([A-z]*)', data['status'])[0]
                    data['vulnerabilityNum'] = [vulnerabilityNum, vulnerabilityLetter]
                    data['ETHLoc'] = tree.find_class('field-name-a-language-of')[0].find('div/div/h2/a').text
                    print('Located language "%s" in Ethnologue' % language)
                    found = True
            else:
                print('Unable to locate language "%s" in Ethnologue' % language)

            if found:
                if data['UNESCOLoc'] == '-' and data['ETHLoc'] == '-':
                    data['location'] = '-'
                elif data['UNESCOLoc'] == '-' and not data['ETHLoc'] == '-':
                    data['location'] = data['ETHLoc']
                elif not data['UNESCOLoc'] == '-' and data['ETHLoc'] == '-':
                    data['location'] = data['UNESCOLoc']
                elif not data['UNESCOLoc'] == '-' and not data['ETHLoc'] == '-':
                    if data['ETHLoc'] in data['UNESCOLoc']:
                        data['location'] = data['UNESCOLoc']
                    else:
                        data['location'] = '%s & %s' %(data['ETHLoc'], data['UNESCOLoc'])
                languageData.append(performReplacements(data))

        languageData = sorted(languageData, key=lambda x: (x['vulnerabilityNum'], 1 / (int(str(x['population']).replace(',', '')) + 1) if str(x['population']).replace(',', '').isdigit() else 0), reverse=True)

        for langData in languageData:
            table += '\n|-\n|| {name} \n|align="center"| <code>{code}</code> \n|| {location} \n|align="right"| {population} \n|| {status} \n|| {vulnerability}'.format(**langData)

        table += '\n|}'
        print(table)
    else:
        raise NotImplementedError
