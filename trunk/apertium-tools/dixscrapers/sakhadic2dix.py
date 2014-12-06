#!/usr/bin/python3
# -*- coding: utf-8 -*-

import fileinput
import itertools
import re
import xml.etree.cElementTree as ET

BRACKETS_RE = re.compile(r'(\(.+?\)|\[.+?\])')
PAGENUMBER_RE = re.compile(r'^\d+$')
SPLIT_RE = re.compile(r'[;,/]\s*')

ABBRVS = {
    'a.': ['adj'],
    'adv.': ['adv'],
    'arch.': [],
    # cf. see also -- has been wiped out
    'comp.': [],
    # conv. converb, modifying verb -- covered later
    'dial.': [],
    'det.': ['det'],
    'Evk.': [],
    'exc.': ['ij'],
    'int.': ['itg'],
    'Mongo.': [],
    'n.': ['n'],
    'num.': ['det', 'qnt'],
    'ono.': [],
    'pl.': ['pl'],
    'pp.': ['post'],
    'pro.': ['prn'],
    'Russ.': [],
    'v.': ['v', 'TD']
}

def insert_blanks(element, line):
    words = line.split()
    if not words:
        return
    element.text = words[0]
    element.tail = None
    blank = None
    for i in words[1:]:
        blank = ET.SubElement(element, 'b')
        blank.tail = i

def is_page_num(line):
    return PAGENUMBER_RE.match(line)

def strip_brackets(line):
    brackets = BRACKETS_RE.search(line)
    if brackets:
        for bracket in brackets.groups():
            line = line.replace(bracket, "")
    return line

def is_cyrillic(word):
    num_non_cyrillic = 0
    num_cyrillic = 0
    for c in word:
        ordc = ord(c)
        if 0x0400 <= ordc <= 0x04FF:
            num_cyrillic += 1
        else:
            num_non_cyrillic += 1
    return num_cyrillic > num_non_cyrillic

def is_capital(word):
    return word[0].isupper()

def split(line):
    return SPLIT_RE.split(line)

class Entry(object):
    def __init__(self, line):
        tags = line.split()

        self.words = []
        self.abbrvs = []
        self.meanings = []

        found_cf = False
        found_conv = False
        for tag in tags:
            if tag in ABBRVS.keys(): # abbreviations
                self.abbrvs.extend(ABBRVS[tag])
                continue
            elif tag == "conv.":
                found_conv = True
                self.abbrvs.append("vaux")
                continue

            if tag == "cf":
                found_cf = True
                continue

            if is_cyrillic(tag) and not found_cf: # entrys
                self.words.append(tag)
            elif not is_cyrillic(tag): # translated
                self.meanings.append(tag)

        # if there's "cf" in a word, we trim off everything else
        for i, word in enumerate(self.words):
            if word == "cf":
                self.words = self.words[:i]

        # if there's a converb, just look at the last word
        if found_conv:
            self.words = self.words[-1]
        else:
            self.words = " ".join(self.words)
        self.meanings = " ".join(self.meanings)

        self.words = strip_brackets(self.words)
        self.meanings = strip_brackets(self.meanings)

        # preprocessing meanings
        if "n" in self.abbrvs and is_capital(self.words[0]):
            self.abbrvs = [abbrv for abbrv in self.abbrvs if not abbrv == "n"]
            self.abbrvs.extend(["np", "XX"])
        self.meanings = self.meanings.replace("to", "")

        # split up meanings and entrys
        self.words = [x.strip() for x in split(self.words) if x.strip()]
        self.meanings = [x.strip() for x in split(self.meanings) if x.strip()]

        if not self.abbrvs:
            self.abbrvs = ['XX']

def preprocess(lines):
    def preprocess_line(line):
        if not line:
            return None
        line = line.strip()
        line = line.replace("•", "")
        line = line.replace("=", "")
        line = line.replace("cf.", "cf")
        line = strip_brackets(line)
        if not line or is_page_num(line):
            return None
        return line

    new_lines = []
    for i, line in enumerate(lines):
        line = preprocess_line(line)
        if not line:
            continue

        # check if next line should be merged with this line
        if i+1 < len(lines):
            words = line.split()
            next_line = preprocess_line(lines[i+1])
            if next_line:
                if (len(words) == 1 or
                    not is_cyrillic(next_line.split()[0])):
                    lines[i+1] = line + " " + next_line
                    continue

        orig_word = ""
        for j, word in enumerate(words):
            if j+1 >= len(words):
                continue
            next_word = words[j+1]

            if word.endswith("."):
                orig_word = " ".join(words[:j])
            
            if word.endswith(";"):
                # if semicolon seperates dictionary entries
                if is_cyrillic(next_word):
                    words[j] = word.replace(";", "")
                    line = " ".join(words[:j+1])
                    next_line = " ".join(words[j+1:])
                    lines.insert(i+1, next_line)
                    break
                # if semicolon seperates abbreviations
                elif next_word.endswith("."):
                    words[j] = word.replace(";", "")
                    line = " ".join(words[:j+1])
                    next_line = orig_word + " " + " ".join(words[j+1:])
                    lines.insert(i+1, next_line)
                    break

        line = line.strip()
        if line:
            new_lines.append(line)
    return new_lines

SAKHA_ALPHABET = "".join("""Аа Бб Вв Гг Ҕҕ Дд ДЬдь Ее Ёё Жж Зз Ии Йй Кк
Лл Мм Нн Ҥҥ НЬнь Оо Өө Пп Рр Сс Һһ Тт Уу Үү
Фф Хх Цц Чч Шш Щщ Ъъ Ыы Ьь Ээ Юю Яя""".split())
ENGLISH_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
SYMBOLS = set(tag for _, tags in ABBRVS.items() for tag in tags)

def main():
    dictionary = ET.Element("dictionary")
    comment = ET.Comment(text="CHECK generated dictionary")
    dictionary.append(comment)

    alphabet = ET.SubElement(dictionary, "alphabet")
    alphabet.text = SAKHA_ALPHABET + ENGLISH_ALPHABET

    sdefs = ET.SubElement(dictionary, "sdefs")
    for symbol in sorted(SYMBOLS):
        sdef = ET.SubElement(sdefs, "sdef")
        sdef.set("n", symbol)

    section = ET.SubElement(dictionary, "section")
    section.set("id", "main")
    section.set("type", "standard")

    lines = list(fileinput.input())
    new_lines = preprocess(lines)

    for line in new_lines:
        entry = Entry(line)
        if not (entry.words and entry.abbrvs and entry.meanings):
            continue

        for word, meaning in itertools.product(entry.words, entry.meanings):
            e = ET.SubElement(section, "e")

            comment = ET.Comment(text=line)
            e.append(comment)

            p = ET.SubElement(e, 'p')

            # add word and meaning
            left = ET.SubElement(p, 'l')
            insert_blanks(left, word)

            right = ET.SubElement(p, 'r')
            insert_blanks(right, meaning)

            # add abbreviations
            for abbrv in entry.abbrvs:
                s = ET.Element('s')
                s.set('n', abbrv)
                left.append(s)
                right.append(s)
    ET.dump(dictionary)

main()
