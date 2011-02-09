#!/usr/bin/perl
# 2010/11/21
# Hèctor Alòs i Font
# Ĉi programaĉo legas vortliston kun gramatikaj indikiloj (<n>, <adj>, <np><al> ktp)
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

	my $totalo = 0;
	my $fh = new FileHandle;
	$fh->open ($dosiero) or die "Ne eblas malfermi la dosieron $dosiero";
	while (my $l = $fh->getline) {
		chop $l;
		printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $l, $l, 'Smith__np';
		$totalo++;
	}
	print STDERR "Atenton: oni ne trovas vortojn\n" unless $totalo;
print STDERR "totalo de vortoj = $totalo\n";
	$fh->close;
}
