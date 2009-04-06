#!/usr/bin/perl

@dups;

#open(COMB, "comb.da.dix");

$found = 0;
while ($comb = <STDIN>)
{
	if ($comb =~ /<e lm=/)
	{
		chomp $comb;
		foreach $dup (@dups)
		{
			if ($dup eq $comb)
			{
				$found = 1;
			}
		}
		if ($found == 1) {
			#print $comb . "\n";
			$found = 0;
		}
		else 
		{
			push(@dups, $comb);
			print $comb . "\n";
		}
	}
	else { print $comb; }
}

close(COMB);
