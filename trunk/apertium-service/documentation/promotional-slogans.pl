#!/usr/bin/perl

use warnings;
use strict;

my @net = qw(ARPAnet JANET ChaosNet FidoNet SNA);
my @sys = split/, /, "PDP10, BBC Micro, Lisp Machine, Commodore 64, IBM 360";

my $r = rand $#net;

print "apertium-service is available on $net[$r] using a $sys[$r]\n";

