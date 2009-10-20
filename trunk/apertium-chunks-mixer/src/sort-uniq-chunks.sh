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
if [ $# != 1 ]
then
  echo "Error: Wrong number of parameters" 2>&1
  echo "Sintax: $0 chunks.SL-TL[.gz]" 2>&1
  echo "Note: chunks.SL-TL[.gz] is supposed to be generated with paste-chunks" 2>&1
  exit 1
fi

withoutgz=`echo $1 | sed -re "s/[.]gz$//g"`

if [ $1 = $withoutgz ]
then
  CAT="cat"
else
  CAT="zcat"
fi

$CAT $1 | cut -d' ' -f1 --complement |  sort | uniq -c | sed -re "s/^[ ]+//g" | sort -t '|' -k 4,4 |\
perl -w -e '
use strict;
use warnings;

my $prev_sl="";
my %hash_tl_alg;
my %hash_tl_count;
while (<>) {
  chomp;

  my @f=split / \|\|\| /;

  my $count = $f[0];
  my $sl = $f[1];   
  my $tl = $f[2];   
  my $alg = $f[3];  

  if ($sl ne $prev_sl) {
    if (length($prev_sl)>0) {
      foreach (keys (%hash_tl_alg)) {
        print $hash_tl_count{$_}, " ||| ", $prev_sl, " ||| ", $_, " ||| ", $hash_tl_alg{$_}, "\n";
      }
    }  

    $prev_sl=$sl;
    %hash_tl_alg = ();
    %hash_tl_count = ();

    $hash_tl_alg{$tl}=$alg;
    $hash_tl_count{$tl}=$count;
  } else {
    if (defined($hash_tl_alg{$tl})) {
      $hash_tl_alg{$tl} .= " " . $alg;
    } else {
      $hash_tl_alg{$tl} = $alg;
    }
     
    if (defined($hash_tl_count{$tl})) {
      $hash_tl_count{$tl} += $count;   
    } else {
      $hash_tl_count{$tl} = $count;
    }    
  } 
}        
         
if (length($prev_sl)>0) {
  foreach (keys (%hash_tl_alg)) {
    print $hash_tl_count{$_}, " ||| ", $prev_sl, " ||| ", $_, " ||| ", $hash_tl_alg{$_}, "\n";
  }
}  
'

