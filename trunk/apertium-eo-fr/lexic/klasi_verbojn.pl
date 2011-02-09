#!/usr/bin/perl
# 2011/01/19
# Hèctor Alòs i Font
# Ĉi programaĉo legas
# 1) verboliston
# 2) liston de transitivaj verboj
# 3) liston de netransitivaj verboj
# 4) liston de verboj samtempe transitivaj kaj netransitivaj
# kaj generas strukturon por e-vortaro:
# <e lm="XX"><i>YY</i><par n="ZZ"/></e>
#

use strict;
use FileHandle;

my $dosiero = $ARGV[0] or die "Mankas la ĉefa verbodosiero";
our $dosiero_tr = $ARGV[1] or die "Mankas la dosiero de transitivaj verboj";
our $dosiero_ntr = $ARGV[2] or die "Mankas la dosiero de netransitivaj verboj";
our $dosiero_tr_ntr = $ARGV[3] or die "Mankas la dosiero de transitivaj kaj netransitivaj verboj";

our $totalo = 0;
our $totalo_tr = 0;
our $totalo_ntr = 0;
our $totalo_ser = 0;
our $totalo_tr_ntr = 0;
our $totalo_aliaj = 0;

pritrakti_dosieron ($dosiero);

exit 0;

#
# funkcioj
#

sub pritrakti_dosieron {
	my ($dosiero) = @_;
	my ($lemo, $sercxvorto, @words, $vosto, $par);

	my $fh = new FileHandle;
	$fh->open ($dosiero) or die "Ne eblas malfermi la dosieron $dosiero";
	while (my $l = $fh->getline) {
		chop $l;
		$sercxvorto = $l;
		$lemo = substr ($l, 0, -1);
		@words = split / /, $l;
		if ($#words > 0) {
			$lemo =~ s| |<b/>|og;
			if ($#words == 1 && $words[$#words] eq 'sin' || $#words == 2 && $words[1] eq 'sin' && $words[2] eq 'for') {
				$lemo = substr ($words[0], 0, -1);
				$par = 'i__vbntr';
				shift @words;
				$vosto = join '<b/>', @words;
				printf "<e lm=\"%s\"><i>%s</i><par n=\"%s\"/><p><l><b/>%s</l><r><g><b/>%s</g></r></p></e>\n", $l, $lemo, $par, $vosto, $vosto;
				$totalo_ntr++;
			} elsif ($#words == 1 && ($words[$#words] eq 'pri' || $words[$#words] eq 'de' || $words[$#words] eq 'per'  || $words[$#words] eq 'en' || $words[$#words] eq 'sur' || $words[$#words] eq 'el' || $words[$#words] eq 'je' || $words[$#words] eq 'al')) {
				$lemo = substr ($words[0], 0, -1);
				$par = 'i__vbntr';
				printf "<e lm=\"%s\"><i>%s</i><par n=\"%s\"/><p><l><b/>%s</l><r><g><b/>%s</g></r></p></e>\n", $l, $lemo, $par, $words[1], $words[1];
				$totalo_ntr++;
			} elsif ($#words == 1 && $words[0] =~ /i$/o && $words[1] =~ /i$/o) {
				# tipo "povi esti': eco dependas de la dua verbo
				$lemo = substr ($words[0], 0, -1);
				$par = get_paradigm ($words[1]);
print STDERR "povi esti. l = $l, par = $par\n";
				printf "<e lm=\"%s\"><i>%s</i><par n=\"%s\"/><p><l><b/>%s</l><r><g><b/>%s</g></r></p></e>\n", $l, $lemo, $par, $words[1], $words[1];
			} elsif ($#words == 2 && $words[0] =~ /i$/o && $words[1] eq 'en' && ($words[$#words] =~ /on$/o || $words[$#words] =~ /ojn$/o) || $#words == 2 && $words[0] =~ /i$/o && $words[1] eq 'al' && $words[2] eq 'si') {
				# tipo "enskribi en konton"
print STDERR "enskribi en konton. l = $l\n";
				$lemo = substr ($words[0], 0, -1);
				$par = get_paradigm ($words[0]);
				$lemo = substr ($lemo, 0, -2) if $par eq '/iĝi__vbntr'; # forigo de fina iĝ
				shift @words;
				$vosto = join '<b/>', @words;
				printf "<e lm=\"%s\"><i>%s</i><par n=\"%s\"/><p><l><b/>%s</l><r><g><b/>%s</g></r></p></e>\n", $l, $lemo, $par, $vosto, $vosto;
			} elsif ($words[$#words] =~ /i$/o) {
				# la lasta vorto estas verbo: verŝajne la unua estas adverbo aŭ 'sin'
				# ni provos serĉi ĝin
				if ($#words != 1 || ($words[0] ne 'sin' && $words[0] !~ /e$/o)) {
					# ni avertas, se ne estas tiel
					print STDERR "Warning 1. l = $l, words[$#words] = $words[$#words]\n";
				}
				$sercxvorto = $words[$#words];
				$par = get_paradigm ($sercxvorto);
				$lemo = substr ($lemo, 0, -2) if $par eq '/iĝi__vbntr'; # forigo de fina iĝ
				printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $l, $lemo, $par;
			} elsif ($#words == 1 && $words[0] eq 'esti' && $words[1] =~ /a$/o) {
				# tipo "esti bona'
print STDERR "esti bona. l = $l\n";
				$lemo = substr ($words[0], 0, -1);
				$par = 'ser__vb';
				printf "<e lm=\"%s\"><i>%s</i><par n=\"%s\"/><p><l><b/>%s</l><r><g><b/>%s</g></r></p></e>\n", $l, $lemo, $par, $words[1], $words[1];
				$totalo_ser++;
			} elsif ($words[0] =~ /i$/o && ($words[$#words] =~ /on$/o || $words[$#words] =~ /ojn$/o)) {
				# tipo "spekti televidon"
print STDERR "spekti televidon. l = $l\n";
				$lemo = substr ($words[0], 0, -1);
				$par = 'i__vbntr';
				shift @words;
				$vosto = join '<b/>', @words;
				printf "<e lm=\"%s\"><i>%s</i><par n=\"%s\"/><p><l><b/>%s</l><r><g><b/>%s</g></r></p></e>\n", $l, $lemo, $par, $vosto, $vosto;
				$totalo_ntr++;
			} else {
print STDERR "*******. l = $l, words[$#words] = $words[$#words]\n";
				printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $l, $lemo, 'i__vblex';
				$totalo_aliaj++;
			}
		} else {
			$par = get_paradigm ($sercxvorto);
			$lemo = substr ($lemo, 0, -2) if $par eq '/iĝi__vbntr'; # forigo de fina iĝ
			printf "<e lm=\"%s\">            <i>%s</i><par n=\"%s\"/></e>\n", $l, $lemo, $par;
		}
		$totalo++;
	}
print STDERR "totalo de verboj = $totalo\n";
print STDERR "totalo de trans = $totalo_tr\n";
print STDERR "totalo de netrans = $totalo_ntr\n";
print STDERR "totalo de 'esti' = $totalo_ser\n";
print STDERR "totalo de trans/netrans = $totalo_tr_ntr\n";
print STDERR "totalo de neklasifitaj = $totalo_aliaj\n";
	$fh->close;
}

sub get_paradigm {
	my ($sercxvorto) = @_;

	my $par = get_paradigm_files ($sercxvorto);
	return $par if $par;

	if ($sercxvorto =~ /igi$/o) {
		$totalo_tr++;
		return 'i__vbtr';
	} elsif ($sercxvorto =~ /iĝi$/o) {
		$totalo_ntr++;
		return '/iĝi__vbntr';
	}

	if ($sercxvorto =~ /adi$/o || $sercxvorto =~ /eti$/o || $sercxvorto =~ /egi$/o || $sercxvorto =~ /aĉi$/o) {
		$par = get_paradigm_files ($` . 'i');
#print STDERR "3. sercxvorto = $sercxvorto, $par (per sufikso)\n" if $par;
		return $par if $par;
	}

	if ($sercxvorto =~ /^re/o || $sercxvorto =~ /^ek/o || $sercxvorto =~ /^fin/o || $sercxvorto =~ /^fi/o || $sercxvorto =~ /^fuŝ/o || $sercxvorto =~ /^mal/o  || $sercxvorto =~ /^mis/o || $sercxvorto =~ /^re/o || $sercxvorto =~ /^pli/o || $sercxvorto =~ /^malpli/o || $sercxvorto =~ /^sub/o || $sercxvorto =~ /^antaŭ/o || $sercxvorto =~ /^post/o) {
		$par = get_paradigm_files ($');
#print STDERR "4. sercxvorto = $sercxvorto, $par (per prefikso)\n" if $par;
		return $par if $par;
	}

	$totalo_aliaj++;
	return 'i__vblex';
}

sub get_paradigm_files {
	my ($sercxvorto) = @_;
	my $par;
	if ($sercxvorto eq 'esti') {
		$totalo_ser++;
		$par = 'ser__vb';
	} elsif (`grep -w $sercxvorto $dosiero_tr_ntr`) {
		$totalo_tr_ntr++;
		return 'i__vbtr_ntr';
	} elsif (`grep -w $sercxvorto $dosiero_tr`) {
		$totalo_tr++;
		return 'i__vbtr';
	} elsif (`grep -w $sercxvorto $dosiero_ntr`) {
		$totalo_ntr++;
		return 'i__vbntr';
	} else {
		return '';
	}
}
