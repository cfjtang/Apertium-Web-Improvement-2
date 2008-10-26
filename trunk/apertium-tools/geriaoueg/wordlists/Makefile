ROOT=/build/svnroot/apertium/trunk/
CYENDIX=$(ROOT)/apertium-cy-en/apertium-cy-en.cy-en.dix
LTEXPAND=/build/local/default/bin/lt-expand
TARGETS=cy-en.txt

all: $(TARGETS)

cy-en.txt:
	$(LTEXPAND) $(CYENDIX) | \
	grep -v REGEXP | \
	sed 's/ /_/g' | \
	sed 's/:>:/:/g' | \
	sed 's/:<:/:/g' | \
	sed 's/:/\t/g' | \
	sed 's/<[a-z]*>*\t/\t/g' | \
	sed 's/></./g'  | \
	sed 's/<np.top>\t/\t/g' | \
	sed 's/<n>\t/\t/g' | \
	sed 's/<adj>\t/\t/g' | \
	sed 's/<det>\t/\t/g' | \
	sed 's/<det.dem>\t/\t/g' | \
	sed 's/<det.pos>\t/\t/g' | \
	sed 's/</ (/g' | \
	sed 's/>/) /g' | \
	sort -u | uniq > $@

clean:
	rm -f $(TARGETS)
