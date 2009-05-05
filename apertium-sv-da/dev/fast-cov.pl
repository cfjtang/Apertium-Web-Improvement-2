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


$commandKnw = "cat $hitparade | apertium-destxt | lt-proc $analyser | apertium-retxt | grep -v '*' | awk '{ print \$1 }' | sed 's;\\^\\([0-9]\\+\\)/.*;\\1;' |";
$commandTot = "cat $hitparade | apertium-destxt | lt-proc $analyser | apertium-retxt | awk '{ print \$1 }' | sed 's;\\^\\([0-9]\\+\\)/.*;\\1;' |";

open(COMMANDKNW, $commandKnw);

$known=0;
while ($line = <COMMANDKNW>) {
	chomp;
	$known+=$line;
}
close(COMMANDKNW);

open(COMMANDTOT, $commandTot);

$total=0;
while ($line = <COMMANDTOT>) {
	chomp;
	$total+=$line;
}
close(COMMANDTOT);

print "Coverage: $known/$total*100 = " . $known/$total*100 . "\n";
