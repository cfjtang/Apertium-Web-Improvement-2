as = [9 9 9 9 11 11 12 11 11 13 15 15 15 15 17 19 20 21 23 42];
a = [254 257 253 260 271 256 264 255 256 259 264 263 256 262 263 263 263 263 263 275];
ms = [1783 1627 2746 3004 4302 5134 3138 3544 6403 5038 7009 9082 8046 8254 6885 9185 13338 12447 12401 31235];
m = [1883 1856 1859 1869 1861 1859 1853 1855 1949 1860 1864 1846 1858 1864 1862 1894 1862 1855 1840 1945];
x = 1:1:length(as);

plot(x, log2(as), "-*r;apertium-service;", 
	x, log2(a), "-+r;apertium;",
	x, log2(ms), "-*b;moses-service;",
	x, log2(m), "-+b;moses;");

title ('Comparison in the "Text Length - Time" space', "fontsize", 30);
xlabel ("Text Length (Number of Sentences)", "fontsize", 30);
ylabel ("Time (log_{2}(Milliseconds))", "fontsize", 30);
legend ("location", "northwest");

print comp.png -dpng -mono -solid -F:30;

replot;
