as = [43291 21818 15715 13220 13286 14837 17211 19199 22941 20665 20615 20810 20670 20605 20697 21617];
a = [295303 177203 154783 151162 150089 149960 149781 153124 149776 149647 150371 154888 149003 149097 157362 156152];
aws = [71846 42712 34115 31756 31152 30133 29395 28204 26702 26109 26632 27437 28484 30308 30329 30058];

x = [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16];

plot(x, as, "-*;apertium-service;", 
	x, a, "-+;apertium;",
	x, aws, "-^;apertium-ws;");

title ('Comparison in the "Number of Concurrent Clients - Time" space', "fontsize", 30);
xlabel ("Number of Concurrent Clients", "fontsize", 30);
ylabel ("Time (ms)", "fontsize", 30);
legend ("location", "northwest");
legend ("boxon");
grid ("on");
# ylim ([0, 350]);

print compmt.png -dpng -mono -solid -F:30;

replot;
