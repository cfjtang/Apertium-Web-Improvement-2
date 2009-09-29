all:
	./make_png.sh
	pdflatex freerbmt09
	bibtex freerbmt09
	pdflatex freerbmt09
	pdflatex freerbmt09

clean:
	rm -f *.aux *.bbl *.blg *.log *.pdf *.dvi *.backup *.eps *~
