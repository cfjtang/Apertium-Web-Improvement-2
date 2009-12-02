#! /bin/bash

#wget http://www.classicistranieri.com/spanish/etext99/2donq10.txt
#iconv --from-code=ISO-8859-1 --to-code=UTF-8 2donq10.txt > donq.txt
#split -l 10 -a 5 donq.txt new

time curl --data-urlencode q@donq.txt -d langpair=es-ca http://localhost:8080/ApertiumServerWS/resources/translate

time for i in new*; do curl --data-urlencode q@$i -d langpair=es-ca http://localhost:8080/ApertiumServerWS/resources/translate; done
