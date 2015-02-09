#!/usr/bin/perl

if ($#ARGV != 1) {

	print "Calculates an estimate of the coverage of a monodix based on a hitparade\n";
	print "usage: ./fast-cov.pl <hitparade.txt> <analyzer.bin>\n";
	print "The hitparade should be in the form of `<freq> <word>'\n";
	exit 1;

}

$hitparade = $ARGV[0];
$analyser = $ARGV[1];

unless (-e $hitparade) {
	print "File $hitparade doesn't exist\n";
	exit 2;
}

unless (-e $analyser) {
	print "File $analyser doesn't exist\n";
	exit 3;
}

$command = "cat $hitparade | apertium-destxt | lt-proc $analyser | apertium-retxt |";

open(COMMAND, $command);

$known=0;
$total=0;
while ($line = <COMMAND>) {
    chomp;

    if ($line =~ m/^[^0-9]*([0-9]+)/) {
        $total += $1;
        }

        if ($line =~ m/^[^0-9]*([0-9]+)[^\*]+$/) {
        $known += $1;
    }

}
close(COMMAND);

print "Coverage: $known/$total*100 = " . $known/$total*100 . "\n";
