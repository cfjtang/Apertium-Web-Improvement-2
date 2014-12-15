#!/usr/bin/env python3

import argparse, requests, json, logging, re
import xml.etree.ElementTree as etree

apiURL = 'http://wiki.apertium.org/w/api.php'

def getPage(pageTitle):
    payload = {'action': 'query', 'format': 'json', 'titles': pageTitle, 'prop': 'revisions', 'rvprop': 'content'}
    viewResult = s.get(apiURL, params=payload)
    jsonResult = json.loads(viewResult.text)

    if not 'missing' in list(jsonResult['query']['pages'].values())[0]:
        return list(jsonResult['query']['pages'].values())[0]['revisions'][0]['*']

def editPage(pageTitle, pageContents, editToken):
    payload = {'action': 'edit', 'format': 'json', 'title': pageTitle, 'text': pageContents, 'bot': 'True', 'contentmodel': 'wikitext', 'token': editToken}
    editResult = s.post(apiURL, data=payload)
    jsonResult = json.loads(editResult.text)
    return jsonResult

def login(loginName, password):
    try:
        payload = {'action': 'login', 'format': 'json', 'lgname': loginName, 'lgpassword': password}
        authResult = s.post(apiURL, params=payload)
        authToken = json.loads(authResult.text)['login']['token']
        logging.debug('Auth token: %s' % authToken)

        payload = {'action': 'login', 'format': 'json', 'lgname': args.loginName, 'lgpassword': args.password, 'lgtoken': authToken}
        authResult = s.post(apiURL, params=payload)
        if not json.loads(authResult.text)['login']['result'] == 'Success':
            logging.critical('Failed to login as %s: %s' % (args.loginName, json.loads(authResult.text)['login']['result']))
        else:
            logging.info('Login as %s succeeded' % args.loginName)
            return authToken
    except Exception as e:
        logging.critical('Failed to login: %s' % e)

def getToken(tokenType, props):
    try:
        payload = {'action': 'query', 'format': 'json', 'prop': props, 'intoken': tokenType, 'titles':'Main Page'}
        tokenResult = s.get(apiURL, params=payload)
        token = json.loads(tokenResult.text)['query']['pages']['1']['%stoken' % tokenType]
        logging.debug('%s token: %s' % (tokenType, token))
        return token
    except Exception as e:
        logging.error('Failed to obtain %s token: %s' % (tokenType, e))

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Apertium Wiki Bot")
    parser.add_argument('loginName', help="bot login name")
    parser.add_argument('password', help="bot password")
    args = parser.parse_args()

    s = requests.Session()
    
    authToken = login(args.loginName, args.password)
    moveToken = getToken('move', 'info')
    editToken = getToken('edit', 'info|revisions')
    if not all([authToken, moveToken, editToken]):
        logging.critical('Failed to obtain required token')
        sys.exit(-1)
    
    statsPages = set()
    for page in ['http://wiki.apertium.org/w/index.php?title=Special:PrefixIndex&from=Apertium-nn-nb&prefix=Apertium-', 'http://wiki.apertium.org/w/index.php?title=Special%3APrefixIndex&prefix=Apertium-']:
        dixTree = etree.fromstring(s.get(page).text)
        for cell in dixTree.findall('.//td/a'):
            if re.match(r'Apertium-[a-z]{2,3}-[a-z]{2,3}/stats', cell.text):
                statsPages.add(cell.text)

    print('%s stats page(s)' % len(statsPages))
    
    #with open('test.txt', 'w+') as f:
    #    f.write(' '.join(list(map(lambda x: re.findall('Apertium-([a-z]{2,3}-[a-z]{2,3})', x)[0], statsPages))))
    
    badPages = set()
   
    for statsPage in statsPages:
        payload = {'action': 'query', 'format': 'json', 'eititle': statsPage, 'eilimit': 'max', 'bot': 'True', 'list': 'embeddedin'}
        listResult = s.get(apiURL, params=payload)
        jsonResult = json.loads(listResult.text)
        badPages.update(set(map(lambda x: x['title'], jsonResult['query']['embeddedin'])))
        
    print('%s bad page(s)' % len(badPages))
        
    for badPage in badPages:
        pageContents = getPage(badPage)
        pageContents = re.sub(r'({{#lst:Apertium-[a-z]{2,3}-[a-z]{2,3}/stats\|[a-z]{2,3}-[a-z]{2,3})-stems(}})', lambda x: x.group(0).replace('-stems', '_stems'), pageContents)
        editPage(badPage, pageContents, editToken)
