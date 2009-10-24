as = [43291 21818 15715 13220 13286 14837 17211 19199 22941 20665 20615 20810 20670 20605 20697 21617];
a = [295303 177203 154783 151162 150089 149960 149781 153124 149776 149647 150371 154888 149003 149097 157362 156152];

x = [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16];

plot(x, as, "-*;apertium-service;", 
	x, a, "-+;apertium;");

title ('Comparison in the "Number of Threads - Time" space', "fontsize", 30);
xlabel ("Number of Threads", "fontsize", 30);
ylabel ("Time (ms)", "fontsize", 30);
legend ("location", "northwest");
legend ("boxon");
grid ("on");
# ylim ([0, 350]);

print compmt.png -dpng -mono -solid -F:30;

replot;
