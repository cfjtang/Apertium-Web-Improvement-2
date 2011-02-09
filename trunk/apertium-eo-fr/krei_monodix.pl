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
		$l =~ s|<b/>| |og;
		if ($l =~ m|^([^<]+)<(.+)>$|o) {
			$lemo = $1;
			$paradigmo = $2;
			skribi ($lemo, $paradigmo);
			$totalo++;
		} else {
			print STDERR "Eraro 1: Ne parsita linio $.: $l\n";
		}
	}
	print STDERR "Atenton: oni ne trovas vortojn\n" unless $totalo;
print STDERR "totalo de vortoj = $totalo\n";
	$fh->close;
}

sub skribi {
	my ($lemo, $paradigmo) = @_;
	my $radiko;
	if ($paradigmo eq "n") {
		if ($lemo =~ /oj$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'oj__n';
		} elsif ($lemo =~ /ulo$/o
			|| $lemo =~ /isto$/o
			|| $lemo =~ /ano$/o
			|| $lemo =~ /anto$/o
			|| $lemo =~ /ato$/o
			|| $lemo =~ /into$/o
			|| $lemo =~ /ito$/o
			|| $lemo =~ /onto$/o
			|| $lemo =~ /oto$/o
			|| $lemo =~ /estro$/o
			|| $lemo =~ /ologo$/o) {
			$radiko = substr ($lemo, 0, -1);
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $radiko, 'ino__n';
		} elsif ($lemo =~ /o$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'o__n';
		} else {
			print STDERR "Eraro 2: Ne konata paradigmo en linio $.: $paradigmo, lemo = [$lemo]\n";
		}
	} elsif ($paradigmo eq "n><acr") {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $lemo, 'BBC__n';
	} elsif ($paradigmo eq "adj") {
		if ($lemo =~ /a$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'a__adj';
		} else {	# prefiksoj
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $lemo, 'eks__adj';
		}
	} elsif ($paradigmo eq "adv") {
		if ($lemo =~ /e$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'e__adv';
		} else {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $lemo, 'jam__adv';
		}
	} elsif ($paradigmo eq "pr") {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $lemo, 'al__pr';
	} elsif ($paradigmo eq "cnjadv") {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $lemo, 'after__cnjadv';
	} elsif ($paradigmo eq "ij") {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $lemo, 'adiós__ij';
	} elsif ($paradigmo eq "vblex") {
		if ($lemo =~ /igi$/o) {
			print STDERR "Eraro 5a: Erara paradigmo en lemo $lemo (linio $.): [$paradigmo] (devas esti 'vbtr')\n";
		} elsif ($lemo =~ /iĝi$/o) {
			print STDERR "Eraro 5b: Erara paradigmo lemo $lemo (en linio $.): [$paradigmo] (devas esti 'vbntr')\n";
		} elsif ($lemo =~ /i$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'i__vblex';
		} else {
			print STDERR "Eraro 4a: Ne konata paradigmo en $lemo (linio $.): [$paradigmo]\n";
		}
	} elsif ($paradigmo eq "vbtr") {
		if ($lemo =~ /iĝi$/o) {
			print STDERR "Eraro 6a: Erara paradigmo en lemo $lemo (linio $.): [$paradigmo] (devas esti 'vbntr')\n";
		} elsif ($lemo =~ /i$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'i__vbtr';
		} else {
			print STDERR "Eraro 4b: Ne konata paradigmo en $lemo (linio $.): [$paradigmo]\n";
		}
	} elsif ($paradigmo eq "vbntr") {
		if ($lemo =~ /igi$/o) {
			print STDERR "Eraro 6a: Erara paradigmo en lemo $lemo (linio $.): [$paradigmo] (devas esti 'vbtr')\n";
		} elsif ($lemo =~ /iĝi$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, '/iĝi__vbntr';
		} elsif ($lemo =~ /i$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'i__vbntr';
		} else {
			print STDERR "Eraro 4c: Ne konata paradigmo en $lemo (linio $.): [$paradigmo]\n";
		}
	} elsif ($paradigmo eq "vbtr_ntr") {
		if ($lemo =~ /igi$/o) {
			print STDERR "Eraro 7a: Erara paradigmo en lemo $lemo (linio $.): [$paradigmo] (devas esti 'vbtr')\n";
		} elsif ($lemo =~ /iĝi$/o) {
			print STDERR "Eraro 7b: Erara paradigmo en lemo $lemo (linio $.): [$paradigmo] (devas esti 'vbntr')\n";
		} elsif ($lemo =~ /i$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'i__vbtr_ntr';
		} else {
			print STDERR "Eraro 4d: Ne konata paradigmo en $lemo (linio $.): [$paradigmo]\n";
		}
	} elsif ($paradigmo eq "np><loc") {
		if ($lemo =~ /io$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'Alban/io__np';
		} elsif ($lemo =~ /o$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'Barcelon/o__np';
		} elsif ($lemo =~ /oj$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'And/oj__np';
		} else {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $lemo, 'Barcelona__np';
		}
	} elsif ($paradigmo eq "np><ant") {
		if ($lemo =~ /o$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'Mark/o__np';
		} else {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $lemo, 'Maria__np';
		}
	} elsif ($paradigmo eq "np><al") {
		if ($lemo =~ /o$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'Vikipedi/o__np';
		} elsif ($lemo =~ /oj$/o) {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $`, 'Olimipik/oj__np';
		} else {
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $lemo, $lemo, 'Wikipedia__np';
		}
	} else {
		print STDERR "Eraro 9: Ne konata paradigmo en linio $.: $paradigmo, lemo = [$lemo]\n";
	}
}
