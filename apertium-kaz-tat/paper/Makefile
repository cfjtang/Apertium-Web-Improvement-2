all: paper anon

paper:
	xelatex -papersize a4 paper.tex
	bibtex paper
	cat paper.bbl | sed 's/citename/astroncite/g' > paper.bbl.tmp
	mv paper.bbl.tmp paper.bbl
	xelatex -papersize a4 paper.tex
	xelatex -papersize a4 paper.tex

clean:
	rm -rf paper.aux paper.bbl paper.blg paper.log paper.pdf

anon:
	xelatex -papersize a4 paper-anon.tex
	bibtex paper-anon
	cat paper-anon.bbl | sed 's/citename/astroncite/g' > paper-anon.bbl.tmp
	mv paper-anon.bbl.tmp paper-anon.bbl
	xelatex -papersize a4 paper-anon.tex
	xelatex -papersize a4 paper-anon.tex


