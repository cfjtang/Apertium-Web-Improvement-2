#!/usr/bin/perl
# 2011/01/20
# Hèctor Alòs i Font
# Ĉi programaĉo legas
# 1) bidix
# 2) liston de transitivaj verboj
# 3) liston de netransitivaj verboj
# 4) liston de verboj samtempe transitivaj kaj netransitivaj
# kaj rekreas la bidix surbaze de la klasifiko de la e-verboj
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
	my ($lemo, $sercxvorto, @words, $vosto, $par, $grupo, $format, $grupo);

	my $fh = new FileHandle;
	$fh->open ($dosiero) or die "Ne eblas malfermi la dosieron $dosiero";
	while (my $l = $fh->getline) {
		chop $l;
		unless ($l =~ m|<s n="vblex"/></l>|o) {
			# ne estas e-verbo
			print $l, "\n";
			next;
		}
		if ($l =~ m|<l>(.*)<g><b/>(.*)</g><s n="vblex"/></l>|o) {
			# verbogrupo
			$sercxvorto = $lemo = $1;
			$grupo = $2;
			$format = $` . '<l>%s<g><b/>%s</g><s n="%s"/></l>' . $' . "\n";
			@words = split /<b\/>/, $grupo;
			unshift @words, $sercxvorto;
			$grupo = 1;	# por testi, ke ne estas eraro en la fontdosiero
		} elsif ($l =~ m|<l>(.*)<s n="vblex"/></l>|o) {
			# simpla verbo (aux senŝanĝaj komencaj vortoj kun fina verbo)
			$sercxvorto = $lemo = $1;
			$format = $` . '<l>%s<s n="%s"/></l>' . $' . "\n";
			@words = split /<b\/>/, $lemo;
			$grupo = 0;	# por testi, ke ne estas eraro en la fontdosiero
		} else {
			# eraro: e-verbo, tamen nek verbogrupo nek simpla verbo
			print STDERR "ERARO 1 en linio $.: $l\n";
			next;
		}

		# la cetero estas kiel en klasi_verbojn.pl, krom en la 'printf' kaj la forigo de la lasta litero de la lemo
		# ... kaj en la nomo de la 'paradigmo'
		if ($#words > 0) {
			if ($#words == 1 && $words[$#words] eq 'sin' || $#words == 2 && $words[1] eq 'sin' && $words[2] eq 'for') {
				$par = 'vbntr';
				shift @words;
				$vosto = join '<b/>', @words;
				printf $format, $lemo, $vosto, $par if $grupo;
#printf STDERR "1." . $format, $lemo, $vosto, $par;
				print STDERR "ERARO 2 en linio $.: $l\n" unless $grupo;
				$totalo_ntr++;
			} elsif ($#words == 1 && ($words[$#words] eq 'pri' || $words[$#words] eq 'de' || $words[$#words] eq 'per'  || $words[$#words] eq 'en' || $words[$#words] eq 'sur' || $words[$#words] eq 'el' || $words[$#words] eq 'je' || $words[$#words] eq 'al')) {
				$par = 'vbntr';
				shift @words;
				$vosto = join '<b/>', @words;
				printf $format, $lemo, $vosto, $par if $grupo;
#printf STDERR "2." . $format, $lemo, $vosto, $par;
				print STDERR "ERARO 3 en linio $.: $l\n" unless $grupo;
				$totalo_ntr++;
			} elsif ($#words == 1 && $words[0] =~ /i$/o && $words[1] =~ /i$/o) {
				# tipo "povi esti': eco dependas de la dua verbo
				$par = get_paradigm ($words[1]);
#print STDERR "povi esti. l = $l, par = $par\n";
				printf $format, $lemo, $vosto, $par if $grupo;
#printf STDERR "3." . $format, $lemo, $vosto, $par;
				print STDERR "ERARO 4 en linio $.: $l\n" unless $grupo;
			} elsif ($#words == 2 && $words[0] =~ /i$/o && $words[1] eq 'en' && ($words[$#words] =~ /on$/o || $words[$#words] =~ /ojn$/o) || $#words == 2 && $words[0] =~ /i$/o && $words[1] eq 'al' && $words[2] eq 'si') {
				# tipo "enskribi en konton"
#print STDERR "enskribi en konton. l = $l\n";
				$par = get_paradigm ($words[0]);
				shift @words;
				$vosto = join '<b/>', @words;
				printf $format, $lemo, $vosto, $par if $grupo;
#printf STDERR "4." . $format, $lemo, $vosto, $par;
				print STDERR "ERARO 7 en linio $.: $l\n" unless $grupo;
			} elsif ($words[$#words] =~ /i$/o) {
				# la lasta vorto estas verbo: verŝajne la unua estas adverbo aŭ 'sin'
				# ni provos serĉi ĝin
				if ($#words != 1 || ($words[0] ne 'sin' && $words[0] !~ /e$/o)) {
					# ni avertas, se ne estas tiel
					print STDERR "Warning 1. l = $l, words[$#words] = $words[$#words]\n";
				}
				$sercxvorto = $words[$#words];
				$par = get_paradigm ($sercxvorto);
				printf $format, $lemo, $par unless $grupo;
				print STDERR "ERARO 5 en linio $.: $l\n" if $grupo;
			} elsif ($#words == 1 && $words[0] eq 'esti' && $words[1] =~ /a$/o) {
				# tipo "esti bona'
#print STDERR "esti bona. l = $l\n";
				$par = 'vbser';
				printf $format, $lemo, $words[1], $par if $grupo;
#printf STDERR "5." . $format, $lemo, $vosto, $par;
				print STDERR "ERARO 6 en linio $.: $l\n" unless $grupo;
				$totalo_ser++;
			} elsif ($words[0] =~ /i$/o && ($words[$#words] =~ /on$/o || $words[$#words] =~ /ojn$/o)) {
				# tipo "spekti televidon"
#print STDERR "spekti televidon. l = $l\n";
				$par = 'vbntr';
				shift @words;
				$vosto = join '<b/>', @words;
				printf $format, $lemo, $vosto, $par if $grupo;
#printf STDERR "6." . $format, $lemo, $vosto, $par;
				print STDERR "ERARO 8 en linio $.: $l\n" unless $grupo;
				$totalo_ntr++;
			} else {
print STDERR "*******. l = $l, words[$#words] = $words[$#words]\n";
				$totalo_aliaj++;
			}
		} else {
			$par = get_paradigm ($sercxvorto);
			printf $format, $lemo, $par unless $grupo;
				print STDERR "ERARO 9 en linio $.: $l\n" if $grupo;
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
		return 'vbtr';
	} elsif ($sercxvorto =~ /iĝi$/o) {
		$totalo_ntr++;
		return 'vbntr';
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
	return 'vblex';
}

sub get_paradigm_files {
	my ($sercxvorto) = @_;
	my $par;
	if ($sercxvorto eq 'esti') {
		$totalo_ser++;
		$par = 'vbser';
	} elsif (`grep -w $sercxvorto $dosiero_tr_ntr`) {
		$totalo_tr_ntr++;
		return 'vbtr_ntr';
	} elsif (`grep -w $sercxvorto $dosiero_tr`) {
		$totalo_tr++;
		return 'vbtr';
	} elsif (`grep -w $sercxvorto $dosiero_ntr`) {
		$totalo_ntr++;
		return 'vbntr';
	} else {
		return '';
	}
}
