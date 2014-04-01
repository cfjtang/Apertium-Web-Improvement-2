
all:          example.dvi

example.ps:   example.dvi
	dvips     example -o

example.dvi:  example.tex example-ch00.tex example-ch01.tex example-ch02.tex \
              example-ch01/ch01.tex example-ch01/ch01.bib \
              example-ch02/ch02.tex example-ch02/ch02.bib \
              example-index.tex
	latex     example
	bibtex    example-ch01
	bibtex    example-ch02
	makeindex example
	latex     example
	latex     example
	makeindex example
	latex     example

clean:
	rm -f *.ilg *.dvi *~ *.blg *.log

veryclean:
	rm -f *.ilg *.dvi *~ *.blg *.log *.ind *.bbl *.idx  *.toc *.aux

