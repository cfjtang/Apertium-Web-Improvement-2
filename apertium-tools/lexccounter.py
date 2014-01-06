#!/usr/bin/env python3

import argparse, re, sys, urllib.request, logging

def countStems(dictionary):
    logger = logging.getLogger(__name__)
    nums = [0, 0]
    currentLexicon = None
    validLexicons = []
    lemmas = set()
    for lineNo, line in enumerate(dictionary.splitlines()):
        line = line.strip()
        if not line.startswith('!') and line:
            line = line.replace('%', '')
            if line.startswith('LEXICON'):
                logger.info('Switching lexicon from %s to %s' % (currentLexicon, line.split()[1]))
                currentLexicon = line.split()[1]
            elif currentLexicon == 'Root' and ';' in line:
                validLexicons.append(line.split(';')[0].strip())
                logging.info('Counting lexicons %s' % validLexicons)
            elif any([line.startswith(_) for _ in ['Multichar_Symbols', 'END']]):
                logging.info('Encountered reserved keyword on L%s: %s' % (lineNo + 1, line))
            else:
                if currentLexicon in validLexicons:
                    try:
                        if ':' in line:
                            split = re.findall(r'^(.+?):([^;]+)', line)
                            if len(split):
                                split = split[0]
                                lemma = split[0].strip()
                                lemmas.add(lemma)
                                tags = split[1].strip().split()[-1].split('-')
                                nums[0] += 1
                                #logger.info('Parsed L%s (%s) as lemma: %s and tags: %s' % (lineNo + 1, line, lemma, tags))
                        else:
                            split = line.split(';')[0].strip().split()
                            lemma = split[0]
                            lemmas.add(lemma)
                            tags = split[1].strip().split('-')
                            nums[0] += 1
                            #logger.info('Parsed L%s (%s) as lemma: %s and tags: %s' % (lineNo + 1, line, lemma, tags))
                    except:
                        logger.warning('Failed to parse L%s: %s' % (lineNo + 1, line))
                        nums[1] += 1
                else:
                    logger.info('Skipping L%s since in lexicon %s' % (lineNo + 1, currentLexicon))
    print('Unique lemmas: %s' % len(lemmas))
    print('Total entries: %s' % nums[0])
    print('Failed to parse %s entries' % nums[1])
    
if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Count stems in a HFST morphological dictionary (lexc)")
    parser.add_argument('uri', help="uri to lexc file")
    parser.add_argument('-v', '--verbose', help="show progress through dictionary (verbose)", action='store_true', default=False)
    args = parser.parse_args()
    
    if args.verbose:
        logging.basicConfig(level=logging.INFO)
    else:
        logging.basicConfig(level=logging.WARNING)
    
    if 'http' in args.uri:
        try:
            dictionary = str((urllib.request.urlopen(args.uri)).read(), 'utf-8')
        except urllib.error.HTTPError:
            print('Dictionary %s not found' % args.uri)
            sys.exit(-1)
        countStems(dictionary)
    else:
        try:
            with open(args.uri) as dictionary:
                countStems(dictionary.read())
        except FileNotFoundError:
            print('Dictionary %s not found' % args.uri)
            sys.exit(-1)