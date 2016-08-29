#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import functools
import unittest
import tempfile
from os.path import join as pjoin
from os.path import abspath, dirname
from subprocess import check_call, check_output, run, PIPE, DEVNULL

from . import streamparser

## Utilities
def tmp(contents):
    t = tempfile.NamedTemporaryFile(mode='w', delete=False)
    t.write(contents)
    return t.name


def rel(fn):
    return abspath(pjoin(dirname(abspath(__file__)), fn))


APERTIUM_TAGGER = rel("../../apertium/apertium-tagger")


def check_stderr(*popenargs, timeout=None, **kwargs):
    return run(*popenargs, stderr=PIPE, timeout=timeout, check=True,
               **kwargs).stderr


def trace_dec(f):
    @functools.wraps(f)
    def inner(*args, **kwargs):
        if len(args) > 0:
            print("run " + " ".join(args[0]))
        return f(*args, **kwargs)
    return inner

check_call = functools.partial(trace_dec(check_call), universal_newlines=True)
check_output = functools.partial(trace_dec(check_output), universal_newlines=True)
check_stderr = functools.partial(trace_dec(check_stderr), universal_newlines=True)

## Test files
DIC = """
^the/the<det><def><sp>$
^books/book<n><pl>/book<vblex><pri><p3><sg>$
^has/have<vbhaver><pres><p3><sg>$
^booked/book<vblex><pp>/book<vblex><past>$
^close/close<adj><sint>/close<n><sg>/close<vblex><inf>/close<vblex><pres>/close<vblex><imp>$
^cat/cat<n><sg>$
^room/room<n><sg>$
^red/red<adj><sint>$
^./.<sent>$
""".strip()

TSX = """
<?xml version="1.0" encoding="utf-8"?>
<tagger name="test">
  <tagset>
    <def-label name="DET" closed="true">
      <tags-item tags="det.*"/>
      <tags-item tags="det.*.*"/>
    </def-label> 
    <def-label name="VERB">
      <tags-item tags="vblex.*"/>
      <tags-item tags="vbhaver.*"/>
    </def-label> 
    <def-label name="NOUN">
      <tags-item tags="n.*"/>
    </def-label> 
    <def-label name="ADJ">
      <tags-item tags="adj.*"/>
      <tags-item tags="adj"/>
    </def-label> 
  </tagset>
</tagger>
""".strip()

TRAIN_NO_PROBLEM_UNTAGGED = """
^The/the<det><def><sp>$
^cat/cat<n><sg>$
^books/book<n><pl>/book<vblex><pri><p3><sg>$
^the/the<det><def><sp>$
^room/room<n><sg>$
^./.<sent>$

^The/the<det><def><sp>$
^red/red<adj><sint>$
^cat/cat<n><sg>$
^books/book<n><pl>/book<vblex><pri><p3><sg>$
^the/the<det><def><sp>$
^red/red<adj><sint>$
^room/room<n><sg>$
^./.<sent>$

^The/the<det><def><sp>$
^red/red<adj><sint>$
^cat/cat<n><sg>$
^books/book<n><pl>/book<vblex><pri><p3><sg>$
^the/the<det><def><sp>$
^room/room<n><sg>$
^./.<sent>$
""".strip()

TRAIN_NO_PROBLEM_TAGGED = """
^The/the<det><def><sp>$
^cat/cat<n><sg>$
^books/book<vblex><pri><p3><sg>$
^the/the<det><def><sp>$
^room/room<n><sg>$
^./.<sent>$

^The/the<det><def><sp>$
^red/red<adj><sint>$
^cat/cat<n><sg>$
^books/book<vblex><pri><p3><sg>$
^the/the<det><def><sp>$
^red/red<adj><sint>$
^room/room<n><sg>$
^./.<sent>$

^The/the<det><def><sp>$
^red/red<adj><sint>$
^cat/cat<n><sg>$
^books/book<vblex><pri><p3><sg>$
^the/the<det><def><sp>$
^room/room<n><sg>$
^./.<sent>$
""".strip()

TRAIN_CAT_TO_BE_A_VERB_UNTAGGED = """
^The/The<det><def><sp>$
^falling/fall<vblex><pprs>/fall<vblex><ger>/fall<vblex><subs>$
^cat/cat<n><sg>$
^has/have<vbhaver><pres><p3><sg>$
^booked/book<vblex><pp>/book<vblex><past>$
^books/book<n><pl>/book<vblex><pres><p3><sg>$
^./.<sent>$
^Close/close<adj><sint>/close<n><sg>/close<vblex><inf>/close<vblex><pres>/close<vblex><imp>$
^the/the<det><def><sp>$
^books/book<n><pl>/book<vblex><pri><p3><sg>$
^./.<sent>$
^The/the<det><def><sp>$
^falling/fall<vblex><pprs>/fall<vblex><ger>/fall<vblex><subs>$
^cat/cat<n><sg>$
^has/have<vbhaver><pres><p3><sg>$
^books/book<n><pl>/book<vblex><pres><p3><sg>$
^./.<sent>$
""".strip()

TRAIN_CAT_TO_BE_A_VERB_TAGGED = """
^The/The<det><def><sp>$
^falling/fall<vblex><pprs>$
^cat/cat<n><sg>$
^has/have<vbhaver><pres><p3><sg>$
^booked/book<vblex><pp>$
^books/book<n><pl>$
^./.<sent>$
^Close/close<vblex><imp>$
^the/the<det><def><sp>$
^books/book<n><pl>$
^./.<sent>$
^The/the<det><def><sp>$
^falling/fall<vblex><pprs>$
^cat/cat<n><sg>$
^has/have<vbhaver><pres><p3><sg>$
^books/book<n><pl>$
^./.<sent>$
""".strip()

TEST_SUCCESS = """
^The/the<det><def><sp>$
^cat/cat<n><sg>$
^books/book<n><pl>/book<vblex><pri><p3><sg>$
^the/the<det><def><sp>$
^room/room<n><sg>$
^./.<sent>$
""".strip()

TEST_NEW_AMBG_CLASS = """
^The/the<det><def><sp>$
^cat/cat<n><sg>/cat<adj>$
^books/book<n><pl>/book<vblex><pri><p3><sg>$
^the/the<det><def><sp>$
^room/room<n><sg>$
^./.<sent>$
""".strip()

## Expected strings
EXPECTED_SUBST = """
Error: A new ambiguity class was found. 
Retraining the tagger is necessary so as to take it into account.
Word 'cat'.
New ambiguity class: {NOUN,ADJ}
""".strip()

## Tests
class AmbiguityClassTest(unittest.TestCase):
    def setUp(self):
        self.tsx_fn = tmp(TSX)
        self.dic_fn = tmp(DIC)

    def changing_class_impl(self, flags, model_fn):
        test1 = tmp(TEST_SUCCESS)
        test2 = tmp(TEST_NEW_AMBG_CLASS)
        success_stderr = check_stderr(
            [APERTIUM_TAGGER, '-d'] + flags +
            ['-g', model_fn, test1],
            stdout=DEVNULL)
        self.assertEqual(success_stderr.strip(), "")
        subst_stderr = check_stderr(
            [APERTIUM_TAGGER, '-d'] + flags +
            ['-g', model_fn, test2],
            stdout=DEVNULL)
        self.assertEqual(subst_stderr.strip(), EXPECTED_SUBST)
        ambg_class = check_output(
           [rel('test-find-similar-ambiguity-class'), model_fn],
           input="NOUN ADJ\n")
        substituted_class = set(ambg_class.split(" "))
        # Should get open class
        self.assertSetEqual(substituted_class, set(("VERB", "NOUN", "ADJ")))

    def test_changing_class_hmm_sup(self):
        model_fn = tmp("")
        untagged = tmp(TRAIN_NO_PROBLEM_UNTAGGED)
        tagged = tmp(TRAIN_NO_PROBLEM_UNTAGGED)
        check_call(
            [APERTIUM_TAGGER, '-s', '0', self.dic_fn, untagged, self.tsx_fn,
             model_fn, tagged, untagged])
        self.changing_class_impl([], model_fn)

    def test_changing_class_hmm_unsup(self):
        model_fn = tmp("")
        untagged = tmp(TRAIN_NO_PROBLEM_UNTAGGED)
        check_call(
            [APERTIUM_TAGGER, '-t', '1', self.dic_fn, untagged, self.tsx_fn,
             model_fn])
        self.changing_class_impl([], model_fn)

    def test_changing_class_sliding_window(self):
        model_fn = tmp("")
        untagged = tmp(TRAIN_NO_PROBLEM_UNTAGGED)
        check_call(
            [APERTIUM_TAGGER, '--sliding-window', '-t', '1', self.dic_fn,
             untagged, self.tsx_fn, model_fn])
        self.changing_class_impl(['--sliding-window'], model_fn)

    def test_cat_is_a_verb(self):
        model_fn = tmp("")
        untagged = tmp(TRAIN_CAT_TO_BE_A_VERB_UNTAGGED)
        tagged = tmp(TRAIN_CAT_TO_BE_A_VERB_TAGGED)
        new_ambg_class = tmp(TEST_NEW_AMBG_CLASS)
        check_call(
            [APERTIUM_TAGGER, '-s', '0', self.dic_fn, untagged, self.tsx_fn,
             model_fn, tagged, untagged])
        subst_stdout = check_output(
            [APERTIUM_TAGGER, '-d', '-g', model_fn, new_ambg_class], stderr=DEVNULL)
        lexical_units = streamparser.parse(subst_stdout)
        acceptable = False
        for lexical_unit in lexical_units:
            for reading in lexical_unit.readings:
                if reading.baseform == 'cat' and \
                        ('adj' in reading.tags or 'n' in reading.tags):
                    acceptable = True
        self.assertTrue(
            acceptable,
            "'cat' must be output and tagged as an adjective or a noun.\n" +
            "Actual output:\n{}".format(subst_stdout))
