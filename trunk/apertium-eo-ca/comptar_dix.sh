echo "noms comuns"
grep "<r>.*\"n\"" apertium-eo-ca.eo-ca.dix | grep "<e" | wc -l
echo "noms comuns"
grep "<r>.*\"np\"" apertium-eo-ca.eo-ca.dix | grep "<e" | wc -l
echo "verbs"
grep "<r>.*\"vblex\"" apertium-eo-ca.eo-ca.dix | grep "<e" | wc -l
echo "adjectius"
grep "<r>.*\"adj\"" apertium-eo-ca.eo-ca.dix | grep "<e" | wc -l
echo "adverbis"
grep "<r>.*\"adv\"" apertium-eo-ca.eo-ca.dix | grep "<e" | wc -l
echo "conjuncions"
grep "<r>.*\"cnjadv\"" apertium-eo-ca.eo-ca.dix | grep "<e" | wc -l
echo "preposicions"
grep "<r>.*\"pr\"" apertium-eo-ca.eo-ca.dix | grep "<e" | wc -l
echo "total"
grep "<e" apertium-eo-ca.eo-ca.dix | wc -l
