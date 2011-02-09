#!/usr/bin/perl
# 2010/12/15
# Hèctor Alòs i Font
# Ĉi programaĉo legas vortliston de adjektivoj ekzistantaj en la bidix
# kiuj samtempe estas adverb-radikoj
# kaj generas strukturon por la bidix
# <e lm="XX"><i>YY</i><par n="ZZ"/></e>
#

use strict;
use FileHandle;

my $BIDIX = "~/aplic/apertium/apertium-eo-fr/apertium-eo-fr.eo-fr.dix";
my $dosiero = $ARGV[0] or die "Mankas la lega dosiero";

pritrakti_dosieron ($dosiero);

exit 0;

#
# funkcioj
#

sub pritrakti_dosieron {
	my ($dosiero) = @_;

	my $totalo = 0;
	my @list = ();
	my $fh = new FileHandle;
	$fh->open ($dosiero) or die "Ne eblas malfermi la dosieron $dosiero";
	while (my $l = $fh->getline) {
		chop $l;
		push @list, `grep ">$l<s n=.adj" $BIDIX`;
	}
	$fh->close;
	foreach my $l (@list) {
		$l =~ s|a<|e<|o;
		$l =~ s|"adj"|"adv"|o;
		$l =~ s|<s n="adj"|ment<s n="adv"|o;
		print $l;
		$totalo++;
	}
	print STDERR "Atenton: oni ne trovas vortojn\n" unless $totalo;
print STDERR "totalo de vortoj = $totalo\n";
	$fh->close;
}
