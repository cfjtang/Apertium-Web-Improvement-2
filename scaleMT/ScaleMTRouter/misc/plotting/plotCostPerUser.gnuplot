set terminal postscript eps color enhanced
#set style data histograms
#set style histogram columnstacked
set style fill solid border -1
set output 'plot-costperuser.eps'

plot '/tmp/plotb' using 2:xtic(1) with histogram title 'Cost per user'

