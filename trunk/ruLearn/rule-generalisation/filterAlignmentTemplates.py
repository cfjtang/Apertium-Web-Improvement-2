#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-
#
# Copyright 2009-2016 Víctor M. Sánchez-Cartagena, Felipe Sánchez-Martínez, Universitat d'Alacant
#
# This file is part of ruLearn.
# ruLearn is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# ruLearn is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with ruLearn.  If not, see <http://www.gnu.org/licenses/>.
#

import sys, argparse, ruleLearningLib, math

parser = argparse.ArgumentParser(description='Filters alignment templates.')
parser.add_argument('--min_count',required=True)
parser.add_argument('--not_alignment_template',action='store_true')
parser.add_argument('--discard_none',action='store_true')
parser.add_argument('--filtering_option_for_length')
parser.add_argument('--criterion', default='count')
parser.add_argument('--proportion_max', default='count')
args = parser.parse_args(sys.argv[1:])

mincount=float(args.min_count)
criterion=args.criterion

for line in sys.stdin:
	line=line.rstrip('\n').decode('utf-8')
	parts=line.split('|')
	if len(parts)>1:
		freq=float(parts[0].strip())

		if criterion=="count":
			length=1
		elif criterion=="proportion":
			length=float(args.proportion_max)
		else:
			if args.filtering_option_for_length:
				if args.discard_none and line.find(u"NONE")>-1:
					length=1
				else:
					if args.filtering_option_for_length=="jair":
						length=len(parts[1].strip().split(u"~")[0].strip().split())
					elif args.filtering_option_for_length=="onlyslpos":
						length=len(parts[1].strip().split())
					else:
						length=1
			else:
				at=ruleLearningLib.AlignmentTemplate()
				at.parse('|'.join(parts[1:]))
				length=float(len(at.sl_lexforms))

		if criterion=="prod":
			#if args.not_alignment_template:
			#	print >> sys.stderr, "WARNING: Length cannot be used when input is not an AT"
			c=freq*length
		elif criterion=="log":
			#if args.not_alignment_template:
			#	print >> sys.stderr, "WARNING: Length cannot be used when input is not an AT"
			c=freq*(1.0+math.log(length))
		elif criterion=="proportion":
			c=freq/length
		else:
			c=freq

		#print >>sys.stderr, str(c)

		if c>=mincount:
			if args.discard_none:
				if line.find(u"NONE")==-1:
					print line.encode('utf-8')
			else:
				print line.encode('utf-8')
