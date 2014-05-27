import yaml

import argparse
import re

LINE_LIMIT = 100000 # how many lines to store before flushing to file

### FUNCTIONS ###
re_strip = re.compile("<.+?>")
def strip_tags(text):
	# supposedly "bad practice"
	# but it will work perfectly fine for this scenario
	return re_strip.sub("", text)

def normalize(text, lang):
	if lang == "zh":
		from mafan import simplify
		text = simplify(text).encode("utf-8")

	return strip_tags(text).strip()

def flush(filename, lines):
	with open(filename + ".norm", "a") as f:
		f.write('\n'.join(lines))
### FUNCTIONS ###

p = argparse.ArgumentParser(description="normalize text before passing through lang-test.py")
p.add_argument('-c', '--config', required=True, help='config file')

args = p.parse_args()
config = yaml.load(open(args.config))

if 'coverage' not in config:
	config['coverage'] = {}

langs = [lang for lang in config['langs']]
langs.sort() # i like sorted languages
# they make life easier

for lang in langs:
	filename = config['langs'][lang]
	if filename[-5:] == ".norm":
		continue # it's already normalized

	num_lines = 0
	with open(filename) as f:
		for line in f:
			num_lines += 1

	# empty normalized file
	f = open(filename + ".norm", "w")
	f.close()

	norm_lines = []

	i = 0
	j = 0
	with open(filename) as f:
		for line in f:
			i += 1
			print("%s: %d/%d\r" % (lang, i, num_lines)),

			line = normalize(line, lang)
			if line:
				j += 1
				norm_lines.append(line)

				if j >= LINE_LIMIT:
					flush(filename, norm_lines)
					
					# reset
					j = 0
					norm_lines = []
	if norm_lines:
		flush(filename, norm_lines)

	config['langs'][lang] = filename + ".norm"

	# empty coverage; it's no longer valid
	if lang in config['coverage']:
		config['coverage'].pop(lang)

	print("Normalized %s to %s.norm" % (filename, filename))

	with open(args.config, 'w') as yaml_file:
		dump = yaml.dump(config, default_flow_style=False)
		yaml_file.write(dump)
