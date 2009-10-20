# Copyright (C) 2009 Universitat d'Alacant / Universidad de Alicante
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License as
# published by the Free Software Foundation; either version 2 of the
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
# 02111-1307, USA.

# Felipe Sánchez-Martínez <fsanchez _at_ dlsi.ua.es>

use strict;
use warnings;

my $insert_missing=1;

if (@ARGV>0) {
  $insert_missing=0 if ($ARGV[0] eq "-noinsert");
}

print STDERR "Insert missing marks? $insert_missing\n";

#Global vars
my @words;

while (<STDIN>) {
  chomp;

  my @aux=split /\s+/;

  @words=();

  foreach (@aux) {
    next if (length($_)==0);
    push @words, $_;
  }

  my $chunk_id;

  for (my $i=0; $i<@words; $i++) {
    $_ = $words[$i];

    #print STDERR "[$i] +$_+\n";

    if(/__\w+__/){ #Mark of chunk
      if(/__BCHUNK_[0-9]+_[0-9]+_[0-9]+__/){
        $i+=&find_end_mark($i);
      } elsif (/__ECHUNK_[0-9]+_[0-9]+_[0-9]+__/){
        $i+=&find_begin_mark($i);
      }
    }
  }

  #Check that chunks marks do not overlap
  for (my $i=0; $i<@words; $i++) {
    if($words[$i] =~ m/__BCHUNK_[0-9]+_[0-9]+_[0-9]+__/){
       &solve_overlapping($i);
    }
  }

  for (my $i=0; $i<@words; $i++) {
    print " " if ($i>0);
    print $words[$i];
  }
  print "\n";
}

sub find_end_mark {
  my ($i) = @_;

  #print STDERR " --- FIND END MARK --- $i\n";
  #&print_words;

  $words[$i] =~ m/[0-9]+_[0-9]+_[0-9]+/;

  my $search_str = "__ECHUNK_$&__";

  my @aux = split /_/, $&;
  my $id=$aux[0];
  my $count=$aux[1];
  my $nwords=$aux[2];

  #print STDERR "BEGIN: $_ ID: $id $count $nwords\n";
  #print STDERR "Search str: $search_str\n";

  my $found = 0;
  for (my $j=$i+1; $j<@words; $j++) {
    #print STDERR "----- > $words[$j]\n";
    if ($words[$j] eq $search_str) {
      $found = 1;
      last;
    }
  }

  my $ret=0;

  unless ($found) {
    if ($insert_missing) {
      #We have to introduce the end-of-chunk mark
      my $j=$i+$nwords+1;

      $j=0 if ($j<0);

      #print STDERR "array size=", scalar @words, "; j=$j\n";
      splice @words, $j, 0, $search_str;
    } else {
      #We have to remove the begin-of-chunk-mark
      splice @words, $i, 1;
      $ret=-1;
    }
  }

  #&print_words;
  return $ret;
}

sub find_begin_mark {
  my ($i) = @_;

  #print STDERR " --- FIND BEGIN MARK --- $i\n";
  #&print_words;

  $words[$i] =~ m/[0-9]+_[0-9]+_[0-9]+/;

  my $search_str = "__BCHUNK_$&__";

  my @aux = split /_/, $&;
  my $id=$aux[0];
  my $count=$aux[1];
  my $nwords=$aux[2];

  #print STDERR "BEGIN: $_ ID: $id $count $nwords\n";
  #print STDERR "Search str: $search_str\n";

  my $found = 0;
  for (my $j=$i-1; $j>=0; $j--) {
    #print STDERR "----- > $words[$j]\n";
    if ($words[$j] eq $search_str) {
      $found = 1;
      last;
    }
  }

  my $ret=0;
  unless ($found) {
    if($insert_missing) {
      #We have to introduce the begin-of-chunk mark
      my $j=$i-$nwords;

      $j=0 if ($j<0);

      #print STDERR "array size=", scalar @words, "; j=$j\n";
      splice @words, $j, 0, $search_str;
    } else {
      #Remove this end-of-chunk mark
      splice @words, $i, 1;
      $ret=-1;
    }
  }

  #&print_words;
  return $ret;
}

sub solve_overlapping {
  my ($i) = @_;

  #print STDERR " --- OVERLAPPING --- $i\n";
  #&print_words;

  $words[$i] =~ m/[0-9]+_[0-9]+_[0-9]+/;

  my $this_mark=$&;
  my $search_str = "__ECHUNK_$&__";

  my $j;
  for ($j=$i+1; $j<@words; $j++) {
    last if ($words[$j] eq $search_str);
  }

  #print STDERR "chunk: $i - $j\n";

  #Search for other chunk marks inside
  my @overlap; #all marks to delete
  for(my $k=$i+1; $k<$j; $k++) {
    $_= $words[$k];
    #print STDERR " ---> $k: $_\n";
    if(/__BCHUNK_[0-9]+_[0-9]+_[0-9]+__/){
      m/[0-9]+_[0-9]+_[0-9]+/;
      push @overlap, $&;
      #print STDERR "     added to overlap\n";
    }
  }

  push @overlap, $this_mark if (@overlap>0);

  #foreach (@overlap) {
  #  print STDERR "In overlap: $_\n";
  #}

  for (my $k=0; $k<@words; $k++) {
    $_=$words[$k];
    for (my $l=0; $l<@overlap; $l++) {
      if(/__[BE]CHUNK_$overlap[$l]__/) {
        #print STDERR "pos to remove: $k\n";
        splice @words, $k, 1;
        $k--;
      }
    }
  }

  #&print_words;
}

sub print_words {
  for my $i (0..$#words) {
    print STDERR "[$i] ", $words[$i], "\n";
  }
  print STDERR "------------------\n";
}

