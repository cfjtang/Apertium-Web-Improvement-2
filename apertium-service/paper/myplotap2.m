as = [9 10 12 11 14 16 18 19 22 21 23 24 28 32 31 34 34 38 38 43];
# a = [306 272 270 255 259 260 261 265 264 263 262 292 267 268 266 277 268 271 271 275];
a = [254 257 258 256 257 256 270 260 261 261 262 315 276 271 267 267 265 274 270 271];
aws = [33 34 36 36 37 39 40 41 44 43 44 45 47 50 49 51 51 54 55 58];

ms = [1093 1904 3996 4493 6761 4945 10048 11285 11591 14751 12230 12833 13971 22779 18743 18771 21482 23678 27850 30773];
m = [1942 1950 1882 1853 1859 1852 1846 1889 1840 1853 1853 1846 1846 1848 1842 1848 1850 1856 1857 1862];

x = [33 66 99 132 165 198 231 264 297 330 364 398 429 464 496 532 569 621 652 679];

plot(x, as, "-*;apertium-service;", 
	x, a, "-+;apertium;",
	x, aws, "-^;apertium-ws;");

title ('Comparison in the "Sentence Length - Time" space', "fontsize", 30);
xlabel ("Sentence Length (string length)", "fontsize", 30);
ylabel ("Time (ms)", "fontsize", 30);
legend ("location", "northwest");
legend ("boxon");
grid ("on");
ylim ([0, 350]);

print compap.png -dpng -mono -solid -F:30;

replot;

myplotmt2

