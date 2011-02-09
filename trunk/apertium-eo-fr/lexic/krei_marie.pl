#!/usr/bin/perl
# 2010/12/15
# Hèctor Alòs i Font
# Ĉi programaĉo legas vortliston de inaj nomoj
# kaj generas strukturon por e-vortaro:
# <e lm="XX"><i>YY</i><par n="ZZ"/></e>
#

use strict;
use FileHandle;

my $dosiero = $ARGV[0] or die "Mankas la lega dosiero";

pritrakti_dosieron ($dosiero);

exit 0;

#
# funkcioj
#

sub pritrakti_dosieron {
	my ($dosiero) = @_;
	my ($lemo, $paradigmo);
	my ($i, $uc);

	my $totalo = 0;
	my $fh = new FileHandle;
	$fh->open ($dosiero) or die "Ne eblas malfermi la dosieron $dosiero";
	while (my $l = $fh->getline) {
		chop $l;
		$uc = 0;
		for ($i=1; $i < length($l); $i++) {
			if (substr($l, $i, 1) eq ' ' || substr($l, $i, 1) eq '-') {
				$uc = 1;
			} else {
				substr($l, $i, 1) = lc (substr($l, $i, 1)) unless $uc;
				$uc = 0;
			}
		}
		printf "<e lm=\"%s\" a=\"sans accents\">            <i>%s</i><par n=\"%s\"/></e>\n", $l, $l, 'Marie__np';
		$totalo++;
	}
	print STDERR "Atenton: oni ne trovas vortojn\n" unless $totalo;
print STDERR "totalo de vortoj = $totalo\n";
	$fh->close;
}
