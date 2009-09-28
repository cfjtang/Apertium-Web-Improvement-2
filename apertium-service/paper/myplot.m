ay = [11 17 33 45 57 63 72];
my = [130 1741 5868 9067 11807 16068 19375];
x = 1:1:length(ay);

plot(x, log(ay), "-*r;apertium-service;", x, log(my), "-b;moses-service;");

title ('Comparison in the "Text Length - Time" space', "fontsize", 28);
xlabel ("Text Length (Number of Sentences)", "fontsize", 28);
ylabel ("Time (log(Milliseconds))", "fontsize", 28);

# print comp.tex -dtex -mono -solid;
print comp.eps -deps -mono -solid -F:30;

replot;
