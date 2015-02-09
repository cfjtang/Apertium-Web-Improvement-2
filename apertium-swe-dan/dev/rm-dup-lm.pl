#!/usr/bin/perl

%seen;

while ($line = <STDIN>)
{
	if ($line =~ /<e lm=/) {
		unless ($seen{$line} == 1) {
			$seen{$line} = 1;
			print $line;
		}
	}
	else { 
		print $line; 
	}
}
