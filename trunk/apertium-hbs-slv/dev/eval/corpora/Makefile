all:
	cat /home/jernej/projekti/projekti/spanija/work/orwell_text/slsr/orwell_sl.whittle | apertium -d ../../../ sl-sh-morph |tr "$$" "\n"  > corp.morph
	cat corp.morph | wc -l > rezultat
	cat corp.morph | grep -v '\*' | wc -l >> rezultat
small:
	head -n 2 /home/jernej/projekti/projekti/spanija/work/orwell_text/slsr/orwell_sl.whittle | apertium -d ../../../ sl-sh-morph |tr "$$" "\n"  > corp.morph
	cat corp.morph | wc -l > rezultat
	cat corp.morph | grep -v '\*' | wc -l >> rezultat
