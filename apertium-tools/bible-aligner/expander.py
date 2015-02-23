#!/usr/bin/env python3

# takes input like
#    "Genesis 50": "Башталыш 50",
# (as many lines as you'd like)
# and outputs all 50 versions

# to use:
# - run script
# - paste input
# - ctrl+d
# output will be generated and displayed
# - ctrl+c to quit
# also:
# can pipe output from and/or to a file

import fileinput
import re

numRe = re.compile(".*\".* (\d+)\":.*\".* (\d+)\".*")

for line in fileinput.input():
	nums = numRe.match(line).groups()
	if nums[0] == nums[1]:
		for i in range(1, int(nums[0])+1):
			#print(str(i)+" ")#, end="")
			newline = line.strip('\n\r').replace(nums[0], str(i))
			print(newline)
