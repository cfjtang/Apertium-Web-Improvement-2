echo "noms comuns"
grep "<r>.*\"n\"" apertium-eo-es.eo-es.dix | grep "<e" | wc -l
echo "noms propis"
grep "<r>.*\"np\"" apertium-eo-es.eo-es.dix | grep "<e" | wc -l
echo "verbs"
grep "<r>.*\"vblex\"" apertium-eo-es.eo-es.dix | grep "<e" | wc -l
echo "adjectius"
grep "<r>.*\"adj\"" apertium-eo-es.eo-es.dix | grep "<e" | wc -l
echo "adverbis"
grep "<r>.*\"adv\"" apertium-eo-es.eo-es.dix | grep "<e" | wc -l
echo "conjuncions"
grep "<r>.*\"cnjadv\"" apertium-eo-es.eo-es.dix | grep "<e" | wc -l
echo "preposicions"
grep "<r>.*\"pr\"" apertium-eo-es.eo-es.dix | grep "<e" | wc -l
echo "total"
grep "<e" apertium-eo-es.eo-es.dix | wc -l
