#!/usr/bin/env python3
#
# written by Zaphron / Harold Seefeld GCI2014
# https://www.google-melange.com/gci/task/view/google/gci2014/4995638496329728

from xml.etree.ElementTree import ElementTree

from xml.etree.ElementTree import Element

import xml.etree.ElementTree as etree

import xml.dom.minidom as minidom

import codecs

from html.parser import HTMLParser

h = HTMLParser()



# Documentation:

# - The .rtf dictionary was converted to a .txt file using http://document.online-convert.com/convert-to-txt

# - The dictionary .rtf and .txt can be found in "tatar-turkish translation.zip".

# - The .txt version of the dictionary should be named to "dictionary.txt" and placed in the same directory as the script for the python script to work.





# Set Dictionary XML Elements

root = Element("dictionary")

tree = ElementTree(root)

section = etree.SubElement(root, "section")

section.set("id", "main")

section.set ("type", "standard")



# Tatar unique letters

uniqueLetters = ["А", "Ә", "Б", "В", "Г", "Д", "Е", "Ё", "Ж", "Җ", "З", "И", "Й", "К", "Л", "М", "Н", "Ң", "О", "Ө", "П", "Р", "С", "Т", "У", "Ү", "Ф", "Х", "Һ", "Ц", "Ч", "Ш", "Щ", "Ъ", "Ы", "Ь", "Э", "Ю", "Я"]

# Turkish Unique Letters

unique2Letters = ["D", "E", "F", "G", "Ğ", "I", "J", "K", "L", "N", "Ö", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Z", "Ş", "І","Ç"]



# Letters to use for the prefix and suffix

languageFix1 = "ta"

languageFix2 = "tr"



# Get file name of translation data

filename = "dictionary.txt"



# Translation Data

txtData = codecs.open(filename, 'r', 'UTF-8').readlines()

txtSplitData = []

txtValidData = []



# Split Extended Lines

for x in txtData:
    
    x = x.replace("O", "") 

    start = x.find( '(' )

    end = x.find( ')' )

    if start != -1 and end != -1:

        x = x[start:end]



    start = x.find( '{' )

    end = x.find( ')' )

    if start != -1 and end != -1:

        x = x[start:end]

    # Remove Whitespace

    x = x.lstrip()

    x = x.rstrip()

    txtSplitData.append(x)



# Filter out all of the miscellaneous data

for x in txtSplitData: 

    if x == "n":

        continue

    if x == " ":

        continue

    if x == "":

        continue

    if "[" not in x:

        if "]" not in x:

            continue

        continue

    if "]" not in x:

        if "[" not in x:

            continue

        continue



    # Check lines that have 2 or more translations

    if x.count("[") >= 2:

        x = x.replace("   ", " ")

        x = x.replace("  ", " ")

        newWords = x.split(" ")
        passedWords = 0

        b = []

        hasBroken = False

        for l in newWords:

            tatarWord = False

            for y in uniqueLetters:

                l = l.upper()

                if y in l:

                    tatarWord = True

                    if tatarWord and passedWords >= 1:

                        txtValidData.append(' '.join(b).lower())

                        b = []

                        b.append(l.lower().replace("<", "")) 

                        passedWords = 0

                        hasBroken = True

                    break

            if not tatarWord:

                b.append(l.lower().replace("<", ""))

                passedWords += 1

            elif tatarWord and hasBroken:

                hasBroken = False

            else:

                b.append(l.lower())

        continue



    

    # If the x passes all the tests append it to valid data

    txtValidData.append(x)



for x in txtValidData:

    x = x.strip()

    

    if x.startswith("["):

        continue
    
    if ":" in x:
        continue



    x = x.replace("/", "")

    x = x.replace(")", "")

    x = x.replace("(", "")

    x = x.replace("{", "")

    x = x.replace("❖", "")

    x = x.replace("<", "")

    x = x.replace("&", "")

    x = x.replace(" ", "<b/>")

    # Handle multiple definitions

    language1words = x.split("[")

    language2words = x.split("]")


    language1words1 = language1words[0].split(",")

    
    try:

        for language1word in language1words1:
		
            definitions = language2words[1].replace(',', ";").replace('.', ';').split(";")

            if ":" in language1word:

                continue

            # Remove Whitespace

            language1word = language1word.lstrip()

            language1word = language1word.rstrip()

            language1word = language1word.replace(" II", "")

            language1word = language1word.replace(" I", "")

            language1word = language1word.replace(" ii", "")

            language1word = language1word.replace(" ii", "")

            language1word = language1word.strip().replace(" ", "<b/>")




            for definition in definitions:

                # Replace Filter Characters

                definition = definition.replace("[", "")

                definition = definition.replace("]", "")

                definition = definition.replace("1", "")

                definition = definition.replace("2", "")

                definition = definition.replace("3", "")

                definition = definition.replace("4", "")

                definition = definition.replace(";", "")

                definition = definition.replace(",", "")

                definition = definition.replace(".", "")

                definition = definition.replace(" vb", "")


                # Remove Whitespace

                definition = definition.lstrip()

                definition = definition.rstrip()

                if definition.endswith('<b/>'):
                    definition = definition.strip()[:-4]

                if definition.startswith('<b/>'):
                    definition = definition.strip()[4:]

                if language1word.endswith('<b/>'):
                    language1word = language1word.strip()[:-4]

                if language1word.startswith('<b/>'):
                    language1word = language1word.strip()[4:]

                definition = definition.rstrip()

                oldCount = len(language1word)

                language1word = language1word.strip("-").strip("-")

                newCount = len(language1word)

                if oldCount != newCount:
                    if definition.endswith('-mek'):
                        definition = definition.strip()[:-4]

                    if definition.endswith('mek'):
                        definition = definition.strip()[:-3]

                    if definition.endswith('-mak'):
                        definition = definition.strip()[:-4]

                    if definition.endswith('mak'):
                        definition = definition.strip()[:-3]

                language1word = language1word.strip("--")
                language1word = language1word.strip("-")

                definition = definition.strip("-")

                definition = definition.strip().replace(" ", "<b/>")

                successfulInitialWord = False

                for yxl in uniqueLetters:

                    if yxl in language1word.upper():

                        successfulInitialWord = True


                if not successfulInitialWord:

                    continue


                if (language1word.replace("<b/>", "").strip() == "" or definition.replace("<b/>", "").strip() == ""):

                    continue

                definition = definition.lower()

                language1word = language1word.lower()

                

                successfulInitialWord = True

                for yxl in uniqueLetters:

                    if yxl in definition.upper():

                        successfulInitialWord = False
                        

                if not successfulInitialWord:

                    continue

                for yxl in unique2Letters:

                    if yxl in language1word.upper():

                        successfulInitialWord = False
                        

                if not successfulInitialWord:

                    continue

                if (language1word.replace("<b/>", "") == "" or definition.replace("<b/>", "") == ""):

                    continue

                definition = definition.strip("-").strip("--")

                if oldCount != newCount:

                    etree.SubElement(section, "e>" + "<p>" + "<l>" + language1word + '<s n="v"/><s n="TD"/>' + "</l>" + "<r>" + definition + '<s n="v"/><s n="TD"/>' + "</r>" + "</p>" + "</e>")

                    continue

                shouldContinue = False
                    
                for bl in txtSplitData:
                    if language1word.capitalize() in bl and definition.capitalize() in bl:
                        etree.SubElement(section, "e>" + "<p>" + "<l>" + language1word.capitalize() + '<s n="np"/><s n="XX"/>' + "</l>" + "<r>" + definition.capitalize() + '<s n="np"/><s n="XX"/>' + "</r>" + "</p>" + "</e>")
                        shouldContinue = True
                        break
                    elif language1word.capitalize() in bl and definition.capitalize() not in bl: 
                        shouldContinue = True
                        etree.SubElement(section, "e>" + "<p>" + "<l>" + language1word.capitalize() + '<s n="np"/><s n="XX"/>' + "</l>" + "<r>" + definition + '<s n="XX"/>' + "</r>" + "</p>" + "</e>")
                        break
                        
                if shouldContinue == True:
                    continue

                etree.SubElement(section, "e>" + "<p>" + "<l>" + language1word + '<s n="XX"/>' + "</l>" + "<r>" + definition + '<s n="XX"/>' + "</r>" + "</p>" + "</e>")

    except IndexError:
        pass





translationXML = codecs.open("translation.dix", "w", "UTF-8")

xml = etree.tostring(root)

xml = h.unescape(str(xml))

xml = xml.replace("</e> />", "</e>")

xml = xml.replace("</e />", "</e>")

xml = xml.replace("\\", "")

xml = xml.replace("   ","")

xml = xml.replace("b'", "")

xml = xml.replace("'", "")

xml = xml.replace("<<", "")

xml = xml.replace("­", "")

xml = xml.replace("vb.", "")

xml = xml.replace("+", "")

xml = xml.replace("<b/><b/><b/><b/><b/>", "<b/>").replace("<b/><b/><b/><b/>", "<b/>").replace("<b/><b/><b/>", "<b/>").replace("<b/><b/>", "<b/>")

xml = xml.replace("</>", "<b/>")

xml = xml.replace("409", "")

xml = xml.replace(",,","")

xml = xml.replace(",", "")

xml = xml.replace(";", '')

xml = xml.replace("<b/>-<b/>", "").replace("<b/>-", "").replace("<r><b/>", "<r>").replace("<b/></r>", "</r>").replace("<b/><", "<").replace("><b/>", ">")


xml = minidom.parseString(xml).toprettyxml()

    

translationXML.write(str(xml))

translationXML.close()



print ("Dictionary " + languageFix1 + "-" + languageFix2 +  " written to 'translation.dix'.")
