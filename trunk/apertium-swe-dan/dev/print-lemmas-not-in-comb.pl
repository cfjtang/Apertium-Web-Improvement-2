#!/usr/bin/perl

%seen;

open(DA, "../apertium-sv-da.da.dix");
open(COMB, "comb.da.dix");

while ($comb = <COMB>) {
	if ($comb =~ /<e lm=/) {
		unless ($seen{$comb} == 1) {
			$seen{$comb} = 1;
		}
	}
}

while ($da = <DA>) {
	unless ($seen{$da} == 1) {
		print $da;
	}
}

close(DA);
close(COMB);
