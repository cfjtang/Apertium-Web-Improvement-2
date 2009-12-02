#! /bin/bash

wget http://www.classicistranieri.com/spanish/etext99/2donq10.txt
iconv --from-code=ISO-8859-1 --to-code=UTF-8 2donq10.txt > donq.txt
split -l 10 -a 5 donq.txt new

time /home/vitaka/local/bin/apertium es-ca <donq.txt >a

time for i in new*; do /home/vitaka/local/bin/apertium es-ca <$i >a; done
