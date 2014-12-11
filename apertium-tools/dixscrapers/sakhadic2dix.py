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
    'int.': ['XX', 'itg'],
    'Mongo.': [],
    'n.': ['n'],
    'num.': ['det', 'qnt'],
    'ono.': ['ij'],
    'pl.': ['pl'],
    'pp.': ['post'],
    'pro.': ['prn', 'XX'],
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
        self.line = line

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

        # split up meanings and entrys
        self.words = [x.strip() for x in split(self.words) if x.strip()]
        self.meanings = [x.strip() for x in split(self.meanings) if x.strip()]

        if not self.abbrvs:
            self.abbrvs = ['XX']

        for i, meaning in enumerate(self.meanings):
            if meaning.startswith("to "):
                meaning = meaning[3:]
            if meaning == "no translation":
                meaning = None
            if "v" in self.abbrvs or "n" in self.abbrvs:
                words = meaning.split(" ")
                if len(words) > 1:
                    words.insert(1, "<g>")
                    words[-1] += "</g>"
                    meaning = " ".join(words)
            self.meanings[i] = meaning
        self.meanings = [x for x in self.meanings if x]

        # make immutable
        self.words = tuple(self.words)
        self.meanings = tuple(self.meanings)
        self.abbrvs = tuple(self.abbrvs)

    def __eq__(self, other):
        return (self.words == other.words
            and self.meanings == other.meanings
            and self.abbrvs == other.abbrvs)

    def __hash__(self):
        return hash((self.words, self.meanings, self.abbrvs))

def preprocess(lines):
    def preprocess_line(line):
        if not line:
            return None
        line = line.strip()
        line = line.replace("•", "")
        line = line.replace("=", "")
        line = line.replace(";", "; ")
        line = line.replace("cf.", "cf")
        line = line.replace("very very", "very")
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

        abbrv_found = False
        orig_word = ""
        meaning_found = False
        for j, word in enumerate(words):
            if j+1 >= len(words):
                continue
            next_word = words[j+1]

            if word.endswith("."):
                # if we find multiple sections
                if meaning_found:
                    line = " ".join(words[:j])
                    next_line = orig_word + " " + " ".join(words[j:])
                    lines.insert(i+1, preprocess_line(next_line))
                    break
                else:
                    orig_word = " ".join(words[:j])
                    abbrv_found = True
            
            if word.endswith(";"):
                # if semicolon seperates dictionary entries
                if is_cyrillic(next_word):
                    line = " ".join(words[:j+1])
                    next_line = " ".join(words[j+1:])
                    lines.insert(i+1, next_line)
                    break
                words[j] = word.replace(";", "")

            if abbrv_found and not word.endswith("."):
                meaning_found = True

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

    entries = set()
    for line in new_lines:
        entry = Entry(line)
        if not (entry.words and entry.abbrvs and entry.meanings):
            continue
        entries.add(entry)

    for entry in entries:
        for word, meaning in itertools.product(entry.words, entry.meanings):
            e = ET.SubElement(section, "e")
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

            comment = ET.Comment(text=entry.line)
            e.append(comment)

    ET.dump(dictionary)

main()
