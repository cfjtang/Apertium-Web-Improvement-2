all:
	./make_eps.sh
	latex freerbmt09
	bibtex freerbmt09
	latex freerbmt09
	latex freerbmt09
	latex freerbmt09
	dvipdfm freerbmt09

clean:
	rm -f *.aux *.bbl *.blg *.log *.pdf *.dvi *.backup *.eps *~
