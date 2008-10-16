VERSION=0.1

FILES=apertium-blog-translation.php css/apertium.css js/apertium.js README COPYING AUTHORS
all: dist

dist: $(FILES) Makefile
	rm -Rf apertium-blog-translation-$(VERSION)
	mkdir apertium-blog-translation-$(VERSION)
	cp $(FILES) Makefile apertium-blog-translation-$(VERSION)
	tar cvf apertium-blog-translation-$(VERSION).tar apertium-blog-translation-$(VERSION)
	rm -Rf apertium-blog-translation-$(VERSION)
	gzip -9 -f apertium-blog-translation-$(VERSION).tar

zip: $(FILES) Makefile
	rm -Rf apertium-blog-translation-$(VERSION)
	mkdir apertium-blog-translation-$(VERSION)
	cp $(FILES) Makefile apertium-blog-translation-$(VERSION)
	zip apertium-blog-translation-$(VERSION).zip apertium-blog-translation-$(VERSION)/*
	rm -Rf apertium-blog-translation-$(VERSION)

	
