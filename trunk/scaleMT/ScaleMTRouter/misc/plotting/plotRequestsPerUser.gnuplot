set terminal postscript eps color enhanced
#set style data histograms
#set style histogram columnstacked
set style fill solid border -1
set output 'plot-requestsperuser.eps'

plot '/tmp/plota' using 1:xtic(2) with histogram title 'Requests per user'

