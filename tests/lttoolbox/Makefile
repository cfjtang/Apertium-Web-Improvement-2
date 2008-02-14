

REPO=http://apertium.svn.sourceforge.net/svnroot/apertium
CHECKOUT=svn co

CONFIGURE=cmake -D CMAKE_BUILD_TYPE:String=Debug

LTTOOLBOX_EXES=lt-proc lt-expand lt-comp

DICT=apertium-en-ca
DICT_TAG=release-0.8.3

OLD=lttoolbox-old
OLD_TAG=test-1.0

NEW=lttoolbox

all: build_lttoolbox

build_lttoolbox: $(OLD) $(DICT)
	$(CONFIGURE) $(OLD)
	$(MAKE) -C $(OLD)

checkout:
	$(CHECKOUT) $(REPO)/tags/$(DICT)/$(DICT_TAG)/ $(DICT)
	$(CHECKOUT) $(REPO)/tags/lttoolbox/$(OLD_TAG) $(OLD)
