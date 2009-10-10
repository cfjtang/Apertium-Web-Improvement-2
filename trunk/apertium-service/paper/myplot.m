as = [9 9 9 9 11 11 12 11 11 13 15 15 15 15 17 19 20 21 23 42];
a = [254 257 253 260 271 256 264 255 256 259 264 263 256 262 263 263 263 263 263 275];
ms = [1783 1627 2746 3004 4302 5134 3138 3544 6403 5038 7009 9082 8046 8254 6885 9185 13338 12447 12401 31235];
m = [1883 1856 1859 1869 1861 1859 1853 1855 1949 1860 1864 1846 1858 1864 1862 1894 1862 1855 1840 1945];

x = [46 62 76 90 101 111 120 130 140 153 163 178 192 207 226 247 275 309 364 679];

plot(log2(x), log2(as), "-*;apertium-service;", 
	log2(x), log2(a), "-+;apertium;",
	log2(x), log2(ms), "-o;moses-service;",
	log2(x), log2(m), "-x;moses;");

title ('Comparison in the "Sentence Length - Time" space', "fontsize", 30);
xlabel ("Sentence Length (log_{2}(string length))", "fontsize", 30);
ylabel ("Time (log_{2}(ms))", "fontsize", 30);
legend ("location", "northwest");
legend ("boxon");
grid ("on");
ylim ([0, 20]);

print comp.png -dpng -mono -solid -F:30;

replot;
