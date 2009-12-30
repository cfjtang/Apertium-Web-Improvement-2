RULES=$1
BASELINE=$2
TEMP=resul/

for x in `cat $RULES  |sed 's/ /%/g'`; do
	rule=`echo $x | sed 's/%/ /g'`;
	num=`echo $rule  |cut -f2 -d':' | cut -f1 -d' '`;
	echo 'SECTION' > /tmp/rtmp;
	echo $rule >> /tmp/rtmp;
	cg-comp /tmp/rtmp /tmp/rtmp.bin;
	cat /tmp/rtmp;
	cat $BASELINE | cg-proc /tmp/rtmp.bin  |  apertium-tagger -g /home/fran/local/share/apertium/apertium-en-ca/ca-en.prob | /home/fran/local/bin/apertium-pretransfer|/home/fran/local/bin/apertium-transfer /home/fran/local/share/apertium/apertium-en-ca/apertium-en-ca.ca-en.t1x  /home/fran/local/share/apertium/apertium-en-ca/ca-en.t1x.bin  /home/fran/local/share/apertium/apertium-en-ca/ca-en.autobil.bin |/home/fran/local/bin/apertium-interchunk /home/fran/local/share/apertium/apertium-en-ca/apertium-en-ca.ca-en.t2x  /home/fran/local/share/apertium/apertium-en-ca/ca-en.t2x.bin |/home/fran/local/bin/apertium-postchunk /home/fran/local/share/apertium/apertium-en-ca/apertium-en-ca.ca-en.t3x  /home/fran/local/share/apertium/apertium-en-ca/ca-en.t3x.bin |/home/fran/local/bin/lt-proc -g /home/fran/local/share/apertium/apertium-en-ca/ca-en.autogen.bin |/home/fran/local/bin/lt-proc -p /home/fran/local/share/apertium/apertium-en-ca/ca-en.autopgen.bin > $TEMP/$num.out;
	cat $TEMP/$num.out | ./irstlm en.blm > $TEMP/$num.ranked;
done;
	
#
#for rule in file:
#	compile rule;
#	translate ambig sentences;
#	rank ambig sentences;
#	calculate total prob diff. from baseline;
#	score rule;
#
#sort rules by prob difference;
