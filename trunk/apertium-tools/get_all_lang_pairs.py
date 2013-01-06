#!/usr/bin/env python3
import subprocess, re

def get_data_from_svn(url):
	svn_data = str(subprocess.check_output("svn list --xml "+url, stderr=subprocess.STDOUT, shell=True), 'utf-8')

	#Find lang pairs and date last updated
	re_return = re.findall('<name>apertium-(\w{2,3})-(\w{2,3})</name>.*?<date>(\d{4}-\d{2}-\d{2})', svn_data, re.DOTALL)

	keys_list = ["lg1", "lg2", "last_updated", "created", "direction", "repo"]
	return_me = []

	for lang_pair in re_return:
		#We have lang_pairs, now get date created from log
		no_modes_file = False
		try:
			svn_data = str(subprocess.check_output("svn log -r 0:HEAD -l 1 --xml "+url+"apertium-"+lang_pair[0]+"-"+lang_pair[1]+"/modes.xml", stderr=subprocess.STDOUT, shell=True), 'utf-8')
		except:
			#Error, no modes file seemingly...
			no_modes_file = True


		#Find date created
		re_return2 = []
		if no_modes_file:
			re_return2 = ["?"]
		else:
			re_return2 = re.findall('<date>(\d{4}-\d{2}-\d{2}).*?</date>', svn_data, re.DOTALL)


		#Finally, directionality:
		if not no_modes_file:
			try:
				svn_data = str(subprocess.check_output("svn cat "+url+"apertium-"+lang_pair[0]+"-"+lang_pair[1]+"/modes.xml", stderr=subprocess.STDOUT, shell=True), 'utf-8')
			except:
				#hmm strange error....
				no_modes_file = True

		directionality = ""
		if no_modes_file:
			directionality = "?"
		else:
			if "<mode name=\""+lang_pair[0]+"-"+lang_pair[1]+"\"" in svn_data:
				directionality = ">"
			if "<mode name=\""+lang_pair[1]+"-"+lang_pair[0]+"\"" in svn_data:
				directionality = "<"+directionality


		lang_pair_data = [lang_pair[0], lang_pair[1], lang_pair[2], re_return2[0], directionality, url.split("/")[-2]]
		return_me.append(dict(zip(keys_list, lang_pair_data)))
#		print(return_me[-1])


	return return_me



def main():
	lang_pairs = []
	lang_pairs = lang_pairs + get_data_from_svn("http://apertium.svn.sourceforge.net/svnroot/apertium/trunk/")
	lang_pairs = lang_pairs + get_data_from_svn("http://apertium.svn.sourceforge.net/svnroot/apertium/staging/")
	lang_pairs = lang_pairs + get_data_from_svn("http://apertium.svn.sourceforge.net/svnroot/apertium/nursery/")
	lang_pairs = lang_pairs + get_data_from_svn("http://apertium.svn.sourceforge.net/svnroot/apertium/incubator/")

	print("\n".join(lang_pairs))


if __name__ == '__main__':
	main()
