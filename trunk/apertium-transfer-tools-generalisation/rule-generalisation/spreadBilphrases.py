# coding=utf-8
# -*- encoding: utf-8 -*-

import sys,argparse,gzip

parser = argparse.ArgumentParser(description='Spreads bilingual phrases according to their box.')
parser.add_argument('--dir',required=True)
args = parser.parse_args(sys.argv[1:])

dir=args.dir

pack=u""
index=0

idsDict=dict()
fileDesc=None

for line in sys.stdin:
	line=line.strip().decode('utf-8')
	parts=line.split(u"|")
	packStr=parts[0]
	bilphrase=u"|".join(parts[1:])	
	
	if packStr != pack:
		
		#close prev file 
		if pack != u"":
			if fileDesc != None:
				fileDesc.close()
		
		index+=1
		pack=packStr
		
		idsDict[index]=pack
		
		#create a new file
		fileDesc=gzip.open(dir+"/newbilphrases/"+str(index)+".bilphrases.gz","wb")
	fileDesc.write(bilphrase.encode('utf-8')+"\n")

if fileDesc != None:
	fileDesc.close()

#print index
for boxid  in idsDict.keys():
	print str(boxid)+"\t"+idsDict[boxid].encode('utf-8')

### me he quedado por aquí ###
	
	
	
	
