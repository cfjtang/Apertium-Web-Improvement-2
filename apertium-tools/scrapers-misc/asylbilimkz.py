#!/usr/bin/env python3

__author__ = 'Yatharth Agarwal <yatharth999@gmail.com>'

import re
import os
from datetime import datetime
from html.parser import HTMLParser
from urllib.request import urlopen

from lxml import etree
from lxml.cssselect import CSSSelector


url = "http://asyl-bilim.kz/forum"
language = "Kazakh"
outfile = "corpus.xml"
statusfile = "status.log"
namespace = "http://apertium.org/xml/corpus/0.9"
xml_template = """<?xml version="1.0"?>
        <corpus xmlns="{namespace}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="{namespace} http://apertium.org/xml/corpus/0.9/corpus.xsd"
        name="rferl" language="{language}"></corpus>""".format(namespace=namespace, language=language)
br_pat = re.compile(r"<br ?/?>")
html_pat = re.compile(r"<[^>]+>")

forum_sel = CSSSelector("a.forum")
forum_pat = re.compile(r'\d+$')
topic_sel = CSSSelector("a.threadLink")
topic_pat = re.compile(r'(\d+)-1$')
page_sel  = CSSSelector("span.numPages")
page_template = lambda url, page: re.sub(r'\d+^', str(page), url)
post_sel = CSSSelector("table.postTable")
id_sel   = CSSSelector(".postNumberLink")
user_sel = CSSSelector(".postUser")
info_sel = CSSSelector(".postTdTop + .postTdTop")
time_pat = re.compile(r"(\d{2}\.\d{2}\.\d{4}),")
time_format = "%d.%m.%Y, %H:%M"
url_pat  = re.compile(r"({}/(?:\d+-){{4}}\d+)".format(url))
content_sel = CSSSelector(".ucoz-forum-post")


def tree(url_or_text, url=True, parser=etree.HTMLParser(encoding="utf-8")):
    return etree.HTML(urlopen(url_or_text).read() if url else url_or_text, parser=parser)

def text(elem, text_only=False, parser=HTMLParser()):
    return parser.unescape(etree.tostring(elem, encoding='utf-8', method='text' if text_only else 'html').decode('utf-8'))

def match(pat, elem):
    return re.findall(pat, text(elem))[0]

def clean(elem):
    replaced = re.sub(br_pat, '\n', text(elem)).replace('\t', ' ')
    return text(tree(replaced, url=False), text_only=True).strip()

def main():

    # init state vars
    done = [0, 0, 0]
    before = [0, 0, 0]
    redundancy_check = False

    # if interrupted before, resume from previous state
    if os.path.isfile(statusfile):
        print("Restoring state…")
        with open(statusfile) as f:
            before = list(map(int, f.readlines()))
        os.remove(statusfile)

    # else if updating an existing corpus, enable redundancy checking mode
    elif os.path.isfile(outfile):
        redundancy_check = True

    # else completely fresh by creating a new file
    else:
        with open(outfile, 'w') as f:
            f.write(xml_template)

    # parse document
    doc = etree.parse(outfile)
    root = doc.getroot()

    try:

        # loop over forums
        for forum in forum_sel(tree(url)):

            # finish resumption
            if before[0]:
                before[0] -= 1
                done[0] += 1
                continue

            print("\nScraping forum #{}...".format(done[0]))

            # set forum variables
            forum_name = forum.text  # unused
            forum_url  = forum.attrib['href'].replace('./', url + '/')
            forum_id   = re.findall(forum_pat, forum_url)[0]

            # loop over topics
            for topic in topic_sel(tree(forum_url)):
                topic_finished = False

                # finish resumption
                if before[1]:
                    before[1] -= 1
                    done[1] += 1
                    continue

                print("Scraping topic #{}".format(done[1]), end='')

                # set topic attributes
                topic_name = topic.text
                topic_url  = topic.attrib['href']
                topic_id   = re.findall(topic_pat, topic_url)[0]

                # loop over pages
                pages = int(page_sel(tree(topic_url))[0].text)
                for page in range(pages, 0, -1):  # in reverse order to speed up redundancy checks

                    # set page attributes
                    page_url  = page_template(topic_url, page)
                    page_tree = tree(page_url)
                    page_time = str(datetime.now()).replace(' ', 'T')

                    # loop over posts
                    for post in reversed(post_sel(page_tree)):  # in reverse order to speed up redundancy checks

                        # finish resumption
                        if before[2]:
                            before[2] -= 1
                            done[2] += 1
                            continue

                        # set post attributes
                        post_user = user_sel(post)[0].text
                        post_id   = id_sel(post)[0].text
                        post_info = info_sel(post)[0]
                        post_time = match(time_pat, post_info)
                        post_url  = match(url_pat, post_info)
                        post_text = clean(content_sel(post)[0])
                        full_id   = "{}-{}-{}".format(forum_id, topic_id, post_id)

                        # check redundancy
                        if redundancy_check and doc.find("/{{{}}}entry[@id=\"{}\"]".format(namespace, full_id)) is not None:
                            topic_finished = True
                            break

                        # build XML element
                        entry = etree.Element('entry')
                        entry.set('date', post_time)
                        entry.set('timestamp', page_time)
                        entry.set('title', topic_name)
                        entry.set('id', full_id)
                        entry.set('source', post_url)
                        entry.set('author', post_user)
                        entry.text = post_text
                        root.append(entry)
                        doc.write(outfile, encoding='utf-8', xml_declaration=True, pretty_print=True)

                        # increment counter
                        done[2] += 1  # TODO: make this and updating file atomic
                        print(".", end='')

                    # check redundancy
                    if topic_finished:
                        print(" (skipping old posts)", end='')
                        break

                # increment counter
                done[1] += 1
                done[2] = 0
                print()

            # increment counter
            done[0] += 1
            done[1] = done[2] = 0

    # save state
    except KeyboardInterrupt:

        print()
        print("Interrupted; saving state… (delete {} to check for updates)".format(statusfile))
        print("Had finished scraping post #{} in topic #{} under forum #{}.".format(done[2], done[1] + 1, done[0] + 1))

        with open(statusfile, 'w') as f:
            for i in range(3):
                f.write("{}\n".format(before[i] + done[i]))



if __name__ == '__main__':
    main()
