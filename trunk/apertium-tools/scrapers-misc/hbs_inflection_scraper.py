#!/usr/bin/env python3
"""
Connects to http://hjp.novi-liber.hr/, looks for the word(s) 
provided, and tries to download their inflection table.

Just a proof of concept, scrapes nouns, but also will give
(incomplete) or gibberish results for other word types.

Uses mechanicalsoup (https://github.com/hickford/MechanicalSoup).
"""
import sys;
import mechanicalsoup

lexicon_url = "http://hjp.novi-liber.hr/";
browser = mechanicalsoup.Browser()
NEWLINE = "\n"

class Noun:
    def __init__(self, lemma, gender):
        self.lemma = lemma
        self.gender = gender
        self.numbers = [] # Singular/Plural

    def __str__(self):
        result = ""
        result += "Noun: " + self.lemma + "(" + self.gender +")" + NEWLINE
        for number in self.numbers:
            result += number.__str__() + NEWLINE
        return result

class Number:
    def __init__(self, scrapeName, displayName, tagName, gender, cases = []):
        self.scrapeName = scrapeName
        self.displayName = displayName
        self.tagName = tagName
        self.cases = cases
        self.gender = gender
        for case in cases:
            case.number = self;
            case.gender = self.gender

    def __str__(self):
        result = self.displayName + NEWLINE
        for case in self.cases:
            result += case.__str__() + NEWLINE
        return result

class Case:
    def __init__(self, scrapeName, displayName, tagName, number = None):
        self.scrapeName = scrapeName
        self.displayName = displayName
        self.tagName = tagName
        self.surface = ""
        self.number = number
        self.gender = "<G>"

    def __str__(self):
        result = self.surface + self.gender + self.tagName
        return result
        
def getLinksToInflections(soup):
    result = []

    link = soup.find(lambda tag: tag.name == 'a' and "kosi_oblici" in tag.attrs['href'])
    if link != None:
        result.append(link)

    details = soup.findAll(lambda tag: tag.name == 'a' and "detaljnije" in tag.text)
    for detail in details:
        result_page = browser.get(lexicon_url + detail.attrs["href"])
        link = result_page.soup.find(lambda tag: tag.name == 'a' and "kosi_oblici" in tag.attrs['href'])
        if link != None:
            result.append(link)

    return result

def getTableEntryForCase(letter, table):
    entry = None

    listForCase = table.findAll(lambda tag: tag.text.strip() == letter)
    if(len(listForCase) > 0):
        td = listForCase[0].findNextSibling('td')
        if(td != None):
            entry = td.text.strip()
    
    return entry

def getInflectionTable(link, providedLemma):
    soup = browser.get(link).soup
    
    # find all tables containing singular/plural ("jednina"/"množina")
    tables = soup.findAll(lambda tag: tag.name=="table" \
                        and tag.findAll(lambda tag: tag.name=="th" \
                        and("jednina" in tag.text or "množina" in tag.text)))

    lemmaElement = soup.find("font", attrs = {"size":"+2"})
    if lemmaElement != None:
        lemma = lemmaElement.find("b").text
        genderRaw = lemmaElement.find("i").text
        if genderRaw == "m": gender = "<m>"
        elif genderRaw == "ž": gender = "<f>"
        elif genderRaw == "sr": gender = "<nt>"
        else : gender = "<G>"
        
        noun = Noun(lemma, gender)
    else:    
        noun = Noun(providedLemma,"<G>")

    cases = [Case("N", "Nominative:", "<nom>")
             , Case("G", "Genitive:", "<gen>")
             , Case("D", "Dative:", "<dat>")
             , Case("A", "Accusative:", "<acc>")
             , Case("V", "Vocative:", "<voc>")
             , Case("L", "Locative:", "<loc>")
             , Case("I", "Instrumental:", "<ins>")]
    
    noun.numbers = [Number("jednina", "Singular:", "<sg>", noun.gender, cases)
               , Number("množina", "Plural:", "<pl>", noun.gender, cases)]

    if len(tables)>0:
        table = tables[0]
        for number in noun.numbers:
            for case in number.cases:
                surface = getTableEntryForCase(case.scrapeName, table)
                if(surface != None):                    
                    case.surface = surface

    return noun

def scrapeNounInflectionTables(noun):
    result = []
    
    search_form = browser.get(lexicon_url).soup.select("#srchFRM")[0]
    search_form.select("#word")[0]['value'] = noun
    result_page = browser.submit(search_form, lexicon_url)
    
    for link in(getLinksToInflections(result_page.soup)):
        result.append(getInflectionTable(lexicon_url + link.attrs['href'], noun))

    return result;

def usage():
    print
    print ("Usage: python " + sys.argv[0] + " word_to_lookup")
    print ("(Works on nouns and partially on adjectives)")
    print

if(len(sys.argv) != 2):
    usage()
else:
    word = sys.argv[1]    
    result = scrapeNounInflectionTables(word)
    for noun in result:
        print(noun)
