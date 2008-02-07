all:
	mkdir -p po/es/LC_MESSAGES/
	mkdir -p po/en/LC_MESSAGES/
	mkdir -p po/ca/LC_MESSAGES/

	msgfmt po/es.po -o po/es/LC_MESSAGES/apertium-tolk.mo
	msgfmt po/en.po -o po/en/LC_MESSAGES/apertium-tolk.mo
	msgfmt po/ca.po -o po/ca/LC_MESSAGES/apertium-tolk.mo

clean:
	rm -rf po/es/ po/en/ po/ca/
