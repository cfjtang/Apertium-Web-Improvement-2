UNOPKG=unopkg
PKGNAME=oooapertium
MAJOR=0
MINOR=4.0-dev
VERSION=$(MAJOR).$(MINOR)
PACKAGE=$(PKGNAME)-$(VERSION).zip
all: clean build 
build:	
	@echo =============== Building addon file ===========
	zip $(PACKAGE) addon.xcu busmodes.py correct.py description.xml \
		icons/pairs_16.bmp icons/pairs_16h.bmp icons/pairs_26.bmp \
		icons/pairs_26h.bmp icons/pairs.bmp icons/translate_16.bmp \
		icons/translate_16h.bmp icons/translate_26.bmp \
		icons/translate_26h.bmp icons/translate.bmp \
		icons/translatefulldoc*.bmp \
		icons/correct_*.bmp \
		license.txt oooapertium.py fulldoc.py
clean: uninstall
	echo ============== Removing addon file ============
	rm -f $(PACKAGE)
test: clean uninstall build
	echo ============= Installing addon ================

	rm -Rf /tmp/log.*
	unopkg remove -v $(PACKAGE)
	unopkg add -v $(PACKAGE)

	#exec oowriter in debug mode. The logs are in /tmp/log
	PYUNO_LOGLEVEL="ARGS" PYUNO_LOGTARGET="/tmp/log" oowriter
uninstall:
	echo ============ Removing addon ==================
	unopkg remove -v org.openoffice.Office.addon.apertium || true
install: clean build test
