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
	my $f1 = new FileHandle;
	my $f2 = new FileHandle;
	my $f3 = new FileHandle;
	$fh->open ($dosiero) or die "Ne eblas malfermi la dosieron $dosiero";
	$f1->open (">$dosiero.fr.dix") or die "Ne eblas malfermi la dosieron $dosiero.fr.dix";
	$f2->open (">$dosiero.eo-fr.dix") or die "Ne eblas malfermi la dosieron $dosiero.eo-fr.dix";
	$f3->open (">$dosiero.eo.dix") or die "Ne eblas malfermi la dosieron $dosiero.eo.dix";
	while (my $l = $fh->getline) {
		chop $l;
		printf $f1 "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $l, $l, 'Carrefour__np';
		printf $f3 "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $l, $l, 'Wikipedia__np';
		printf $f2 "<e>       <p><l>%s<s n=\"np\"/><s n=\"al\"/></l>   <r>%s<s n=\"np\"/><s n=\"al\"/><s n=\"mf\"/></r></p></e>\n", $l, $l;
		$totalo++;
	}
	print STDERR "Atenton: oni ne trovas vortojn\n" unless $totalo;
print STDERR "totalo de vortoj = $totalo\n";
	$fh->close;
}
