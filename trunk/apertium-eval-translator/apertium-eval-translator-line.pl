#!/usr/bin/perl -w 

# (c) 2006 Felipe S�nchez Mart�nez
# (c) 2006 Universitat d'Alacant
#
# This software calculates the word error rate (WER) and the
# position-independent word error rate (PER) between the translation
# performed by the apertium MT system an a reference translation
# obtained by post-editing the system ouput.
# 
# The edit_distance procedure used in this script is based on 
# the Levenshtein distance implementation by Jorge Mas Trullenque 
# that can be found in http://www.merriampark.com/ldperl2.htm 
#
# This software is licensed under the GPL license version 2, or at
# your option any later version 
#

use strict; 
use warnings;

# Getting command line arguments:
use Getopt::Long;
# Documentation:
use Pod::Usage;
# I/O Handler
use IO::Handle;

use locale;
use POSIX qw(locale_h);
setlocale(LC_ALL,"");

my($test, $ref, $help, $version);

my($nunknown, $ntest, $nref, $distance_nounk, $per_nounk);

my($test_corpus, $ref_corpus);

my(@words_test, @words_ref);

# Command line arguments
GetOptions( 'test|t=s'           => \$test,
            'ref|r=s'            => \$ref,
            'help|h'             => \$help,
	    'version|v'          => \$version,
          ) || pod2usage(2);

if ($version) {
   print "apertium-eval-translator-line 1.1.0\n";
   exit 0;
}

pod2usage(2) if $help;
pod2usage(2) unless ($test);
pod2usage(2) unless ($ref);

open(TEST, "<$test") or die "Error: Cannot open test file \'$test\': $!\n";
open(REF, "<$ref") or die "Error: Cannot open reference file \'$ref\': $!\n";

#print "Test file: '$test'\n";
#print "Reference file '$ref'\n\n";


while(<TEST>) {
  &preprocess;
  $test_corpus=$_;
  $nunknown+=s/[*](\w+)/$1/g;
  @words_test = split /[\s\n]+/;
  $ntest+=@words_test;

  $_=<REF>;
  &preprocess;
  $ref_corpus=$_;
  @words_ref = split /[\s\n]+/;
  $nref+=@words_ref;

  #print "TEST: $test_corpus\n";
  #print "REF:  $ref_corpus\n";
  $distance_nounk+=&edit_distance; 
  #print "WER: ", sprintf("%.2f",($distance_nounk/$ntest)*100), " %\t";
  $per_nounk+=&position_independent_errors;
  #print "PER: ", sprintf("%.2f",($per_nounk/$ntest)*100), " %\n";
  #print "\n";
}

close(TEST);
close(REF);


print "Statistics about input files\n";
print "-------------------------------------------------------\n";
print "Number of words in reference: $nref\n";
print "Number of words in test: $ntest\n";
print "Number of unknown words (marked with a star) in test: $nunknown\n";
print "Percentage of unknown words: ", sprintf("%.2f",($nunknown/$ntest)*100), " %\n";
print "\n";

print "Results when removing unknown-word marks (stars)\n";
print "-------------------------------------------------------\n";
print "Edit distance: $distance_nounk\n";
print "Word error rate (WER): ", sprintf("%.2f",($distance_nounk/$ntest)*100), " %\n";
print "Number of position-independent word errors: ",  $per_nounk, "\n";
print "Position-independent word error rate (PER): ", sprintf("%.2f",($per_nounk/$ntest)*100), " %\n";

print "\n";


sub position_independent_errors {

  #Words that have no matching counterparts are counted as
  #substitution errors. Depending on which corpus, test or reference,
  #is longer the remaining words result in either insertion or
  #deletion errors.  The number of position-independent errors are
  #always less or equal to the edit distance.

  my (%hash_test, %hash_ref);

  foreach (sort @words_test) {
    if (defined($hash_test{$_})) {
      $hash_test{$_}++;
    } else {
      $hash_test{$_}=1;
    }
  }

  foreach (sort @words_ref) {
    if (defined($hash_ref{$_})) {
      $hash_ref{$_}++;
    } else {
      $hash_ref{$_}=1;
    }
  }

  my $ntest = @words_test;
  my $nref = @words_ref;

  my $sum=0;

  foreach (keys %hash_test) {
    if(defined($hash_ref{$_})) {
      $sum+=abs($hash_test{$_}-$hash_ref{$_});
    } else {
      $sum+=$hash_test{$_};
    }
    $hash_test{$_}=0;
    $hash_ref{$_}=0;
  }

  foreach (keys %hash_ref) {
    if(defined($hash_test{$_})) {   
      $sum+=abs($hash_ref{$_}-$hash_test{$_});
    } else {
      $sum+=$hash_ref{$_};
    }
    $hash_ref{$_}=0;
    $hash_test{$_}=0;
  }

  return (abs($ntest-$nref)+$sum)/2;
}

sub edit_distance {
  my @W=(0..@words_ref);
  my ($i, $j, $cur, $next);

  my ($lim_inf, $lim_sup, $best_j);
  $best_j=0;
  for $i (0..$#words_test) {
    $cur=$i+1;

    for $j (0..$#words_ref){
      my $cost=($words_test[$i] ne $words_ref[$j]);
      $next=min([$W[$j+1]+1, $cur+1, $cost+$W[$j]]);
      $W[$j]=$cur;

      $best_j=$j+1 if ($cur > $next);

      $cur=$next;
    }
    $W[@words_ref]=$next;
  }
  return $next;
}

sub min {
  my @list = @{$_[0]};
  my $min = $list[0];

  foreach my $i (@list) {
    $min = $i if ($i < $min);
  }
   return $min;
}

sub preprocess {
  chomp;
  #Insert spaces before and after  punctuation marks 
  #s/([.,;:%�?�!()\[\]{}<>])/ $1 /g;
}


__END__


=head1 NAME

=head1 SYNOPSIS

apertium-eval-translator -test testfile -ref reffile 

Options:

  -test|-t     Specify the file with the translation to evaluate 
  -ref|-r      Specify the file with the reference translation 
  -help|-h     Show this help message
  -version|-v  Show version information and exit
  
Note: Reference translation MUST have no unknown-word marks, even if
      they are free rides.

This software calculates (at document level) the word error rate (WER)
and the postion-independent word error rate (PER) between a
translation performed by the Apertium MT system and a reference
translation obtained by post-editing the system ouput. 

It is assumed that unknow words are marked with a star (*), as
Apertium does; nevertheless, it can be easily adapted to evaluate
other MT systems that do not mark unknown words with a star.

(c) 2006 Felipe S�nchez-Mart�nez
(c) 2006 Universitat d'Alacant

This software is licensed under the GNU GENERAL PUBLIC LICENSE version
2, or at your option any latter version. See
http://www.gnu.org/copyleft/gpl.html for a complete version of the
license.
