ay = [11 17 33 45 57 63 72];
my = [130 1741 5868 9067 11807 16068 19375];
x = 1:1:length(ay);

plot(x, log(ay), "-*r;apertium-service;", x, log(my), "-b;moses-service;");

title ('Comparison in "Number of Sentences - log(Milliseconds)" space');
xlabel ("Number of Sentences");
ylabel ("log(Milliseconds)");

print comp.tex -dtex -mono -solid;

replot;
