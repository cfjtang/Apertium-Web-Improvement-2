import cld2full
import yaml

import argparse
import random

NUM_LINES = 20000 # number of lines to test

### FUNCTIONS ###
def get_correct_lang(text, lang):
	return cld2full.detect(text)[2][0][1] == lang
### FUNCTIONS ###

p = argparse.ArgumentParser(description="cld2 testing system")
p.add_argument('-c', '--config', required=True, help='config file')

args = p.parse_args()
config = yaml.load(open(args.config))

if 'coverage' not in config:
	config['coverage'] = {}

langs = [lang for lang in config['langs']]
langs.sort() # i like sorted languages
# in this case they make life easier

for lang in langs:
	if lang in config['coverage']:
		continue # we already tested this

	count = 0
	bad_lines = []
	filename = config['langs'][lang]

	# generate a list of random numbers
	num_lines = 0
	with open(filename) as f:
		for line in f:
			num_lines += 1

	if num_lines < NUM_LINES:
		# iterate through all
		print("WARNING: Wikipedia corpus text is less than %d. Results may be inaccurate." % NUM_LINES)
		select_lines = range(num_lines)
	else:
		# pick NUM_LINES lines from range
		select_lines = random.sample(xrange(num_lines), NUM_LINES)
		select_lines.sort()

	with open(filename) as f:
		i = 0
		j = 0
		for line in f:
			# check if it's selected.
			if i == select_lines[j]:
				i += 1
				j += 1 # advance to the next line number
				if j >= len(select_lines):
					break # we finished going through all the lines
			else:
				i += 1
				continue

			if get_correct_lang(line, lang):
				count += 1
			else:
				bad_lines.append(line)

			print("%s: %d/%d\r" % (lang, count, len(select_lines))),

		coverage = float(count)/len(select_lines)
		config['coverage'][lang] = coverage

		print("%s: %d/%d %f" % (lang, count, len(select_lines), coverage))
		if count == 0:
			print("\tCLD2 does not support %s." % lang)
		else:
			lines = random.sample(bad_lines, 20)
			for line in lines:
				print("\t%s" % line.strip())
		print("")

		with open(args.config, 'w') as yaml_file:
		    dump = yaml.dump(config, default_flow_style=False)
		    yaml_file.write(dump)

print("All languages have been tested.")