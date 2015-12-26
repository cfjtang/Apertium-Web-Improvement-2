These are two .odt files found, and trimmed, by Darkgaia for GCI2016. These files are in Basque, and when converted into Spanish by Apertium's odt format, result in erroneous files.

These are a result of incorrect structural transfer rules in the Basque -> Spanish stable language pair.

They can be observed in HTML formatting as well. The misplacement of the blanks result in broken formats.

Please see the following echo commands to identify the incorrect structural transfer rule:

dhs@dhs-VERSA-E6501-RNN01254695:~/Desktop/bootstrap-master/apertium-eu-es$ echo "<a>bere</a> <b>pieza</b> <c>hori</c>" | apertium -f html-noent -d . eu-es
<a>Su</a> <b>pieza</c>

dhs@dhs-VERSA-E6501-RNN01254695:~/Desktop/bootstrap-master/apertium-eu-es$ echo "<a>menuko</a> <b>itxura</b>  <c>aukerarekin</c>" | apertium -f html-noent -d . eu-es
<a>Con la opción</b>  <c>de apariencia</a> <b>del menú</c>