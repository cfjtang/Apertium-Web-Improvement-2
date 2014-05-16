import cld2full
import argparse, yaml

def get_correct_lang(text, lang):
	if lang == "zh":
		from mafan import simplify
		text = simplify(text).encode("utf-8")

	return cld2full.detect(text)[2][0][1] == lang

p = argparse.ArgumentParser(description="cld2 testing system")
p.add_argument('-c', '--config', required=True, help='config file')

args = p.parse_args()

counts = {}
total_counts = {}
bad_lines = {}

config = yaml.load(open(args.config))

if 'coverage' not in config:
	config['coverage'] = {}

for lang in config['langs']:
	if 'coverage' in config['langs'][lang]:
		continue # we already tested this

	counts[lang] = 0
	total_counts[lang] = 0
	bad_lines[lang] = []

	for filename in config['langs'][lang]:
			f = open(filename)
			line = f.readline()
			while line:
				if line.strip() == "":
					line = f.readline()
					continue

				if get_correct_lang(line, lang):
					counts[lang] += 1
				else:
					bad_lines[lang].append(line)
				total_counts[lang] += 1

				print("%d/%d\r" % (counts[lang], total_counts[lang])),
				
				line = f.readline()

	coverage = float(counts[lang])/total_counts[lang]
	config['coverage'][lang] = coverage

	print("%s: %d/%d %f" % (lang, counts[lang], total_counts[lang], coverage))
	for line in bad_lines[lang][:20]:
		print("\t%s" % line.strip())

	with open(args.config, 'w') as yaml_file:
	    dump = yaml.dump(config, allow_unicode=True)
	    yaml_file.write(dump)