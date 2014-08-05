import argparse
import os.path, sys
from lib.fms import FMS
from lib.ap import Apertium
from lib.patcher import Patcher
from lib.utilities import assertion
from lib.features import get_features
from lib.phrase_extractor import PhraseExtractor
from lib.utilities import preprocess, assertion, get_subsegment_locs, patch

reload(sys)
sys.setdefaultencoding('utf-8')

parser = argparse.ArgumentParser(description='Regression test for preprocess.py')
parser.add_argument('out', help='Output file generated from test.py')

parser.add_argument('LP', help='Language Pair (sl-tl)')

parser.add_argument('-d', help='Specify the lanuguage-pair installation directory')
parser.add_argument('-v', help='Verbose Mode', action='store_true')
parser.add_argument('--mode', help="Modes('all', 'cam', 'compare')", default='all')
parser.add_argument('--min-fms', help='Minimum value of fuzzy match score of S and S1.', default='0.8')
parser.add_argument('--min-len', help='Minimum length of sub-string allowed.', default='2')
parser.add_argument('--max-len', help='Maximum length of sub-string allowed.', default='5')
args = parser.parse_args()

#Preprocessing
lp = args.LP
lps = lp.split('-')
assertion(len(lps) == 2, "LP should be of type a-b, eg, 'en-eo'")

#Make sure all files exist
assertion(os.path.isfile(args.out), args.out+" doesn't exist")

#TODO:Check lines are equal in SLFs and TLFs.

#Command line params
lp_dir = args.d
verbose = args.v
mode = args.mode.lower()

assertion(mode in ['all', 'cam', 'compare'], "Mode couldn't be identified.")

min_fms = float(args.min_fms)
min_len = int(args.min_len)
max_len = int(args.max_len) 


apertium = Apertium(lps[0], lps[1])
(out, err) = apertium.check_installations(lp_dir)
assertion(out, err)

#Reopen new file
file1 = open(args.out)

#Global values
gl_wer = []
best_wer = []
gl_up_wer = []
gl_no_of_patches = 0.0

if mode == 'compare':
	gl_wer2 = []
	best_wer2 = []
	gl_no_of_patches2 = 0.0

count = 1

while True:
	s = file1.readline()
	t = file1.readline()
	s1 = file1.readline()
	t1 = file1.readline()

	if not (s and s1 and t and t1):
		break

	wer = []
	no_of_patches = 0.0
	if mode == 'compare':
		wer2 = []
		no_of_patches2 = 0.0

	tgt_sentences = t1.lower()
	
	patches = Patcher(apertium, s, s1, t).patch(min_len, max_len)

	unpatched = patches[0]
	up_wer = 1.0 - FMS(unpatched[0].lower(), tgt_sentences).calculate_using_wanger_fischer()
	gl_up_wer.append(up_wer)

	for (patch, features, _, _, _, cam, traces) in patches[0:]:
		if mode == 'all':
			fms = FMS(patch.lower(), tgt_sentences).calculate_using_wanger_fischer()
			wer.append(1.0-fms)
			no_of_patches += 1
		elif mode == 'cam' and cam:
			fms = FMS(patch.lower(), tgt_sentences).calculate_using_wanger_fischer()
			wer.append(1.0-fms)			
			no_of_patches += 1
		elif mode == 'compare':
			fms = FMS(patch.lower(), tgt_sentences).calculate_using_wanger_fischer()
			wer.append(1.0-fms)
			no_of_patches += 1
			if cam:
				fms = FMS(patch.lower(), tgt_sentences).calculate_using_wanger_fischer()
				wer2.append(1.0-fms)
				no_of_patches2 += 1

	if mode == 'compare' and wer2 != []:
		gl_wer += wer
		best_wer.append(min(wer))
		gl_no_of_patches += no_of_patches

		gl_wer2 += wer2
		best_wer2.append(min(wer2))
		gl_no_of_patches2 += no_of_patches2

	elif mode != 'compare':
		gl_wer += wer
		gl_no_of_patches += no_of_patches

		if wer != []:
			best_wer.append(min(wer))

	if verbose and mode != 'compare':
		if wer != []:
			print("#%d Best = %.02f%% Avg = %.02f%% Unpatched = %.02f%% N = %d"
				%(count, min(wer)*100, (sum(wer)/no_of_patches)*100, up_wer*100, int(no_of_patches)))
		else:
			print("#%d Best = %.02f%% Avg = %.02f%% Unpatched = %.02f%% N = %d"
				%(count, up_wer*100, up_wer*100, up_wer*100, int(no_of_patches)))
		count += 1

if mode == 'compare':
	print("Global Statistics(all):")
else:
	print("Global Statistics:")
print("Average best patched WER: {0}".format(sum(best_wer) / (len(best_wer)*1.0)))
print("Average WER: {0}".format(sum(gl_wer) / len(gl_wer)))
print("Average unpatched WER: {0}".format(sum(gl_up_wer)/len(gl_up_wer)))
print("Number of patched sentences: {0}".format(int(gl_no_of_patches)))

if mode == 'compare':
	print("Global Statistics (covering all mismatches):")
	print("Average best patched WER: {0}".format(sum(best_wer2) / (len(best_wer2)*1.0)))
	print("Average WER: {0}".format(sum(gl_wer2) / len(gl_wer2)))
	print("Average unpatched WER: {0}".format(sum(gl_up_wer)/len(gl_up_wer)))
	print("Number of patched sentences: {0}".format(int(gl_no_of_patches2)))

