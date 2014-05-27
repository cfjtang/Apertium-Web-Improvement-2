`lang-test` is meant to test out the coverage of CLD2 for languages supported by Apertium, so as to be able to improve the accuracy of language detection.

To start off, you should install CLD2 (refer to http://blog.xanda.org/2014/04/02/installing-compact-language-detection-2-cld2-on-ubuntu/).

For the corpus, grab a Wikipedia dump and extract the corpus text from it. Take a look at http://wiki.apertium.org/wiki/Wikipedia_Extractor for how to use WikipediaExtractor.

Then, set up a `config.yaml` in the `lang-test` directory that should look something like this:

    langs:
       en: /path/to/corpus
       .
       .
       .

[NOTE: Language codes MUST be ISO 639-1 as that's what CLD2 uses]

Once you're done, type the following command to normalize the text:

    python2 normalize.py -c config.yaml

Then, type the following command to start testing coverage.

    python2 lang-test.py -c config.yaml

The program will output CLD2 coverage for each language and display the first 20 lines which were detected wrongly.

Once a language has been tested, the coverage will be appended onto the config file and the next time you run `lang-test.py`, the languages that have been tested will be skipped.
