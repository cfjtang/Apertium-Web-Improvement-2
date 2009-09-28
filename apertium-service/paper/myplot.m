ay = [11 17 33 45 57 63 72];
my = [130 1741 5868 9067 11807 16068 19375];
x = 1:1:length(ay);

plot(x, log2(ay), "-*r;apertium-service;", x, log2(my), "-b;moses-service;");

title ('Comparison in the "Text Length - Time" space', "fontsize", 20);
xlabel ("Text Length (Number of Sentences)", "fontsize", 20);
ylabel ("Time (log_{2}(Milliseconds))", "fontsize", 20);
legend ("location", "northwest");

print comp.eps -deps -mono -solid -F:20;

replot;
