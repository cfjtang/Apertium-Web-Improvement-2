set terminal postscript eps color enhanced
#set style data histograms
#set style histogram columnstacked
set style histogram
set style fill solid border -1
set output 'plot-requestsperlangpair.eps'
set boxwidth 2
set xtics nomirror rotate by -45 scale 0

plot '/tmp/plota' using 1:xtic(2) with histogram title 'Requests per language pair'

