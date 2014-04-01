import sys

if len(sys.argv) != 1+2:
    print ('\nTakes two files and outputs a cartesian product of their lines')
    print
    print ('Usage:')
    print ('python '+sys.argv[0]+'file1 file2\n')
    sys.exit()

nom=map(lambda x:x.replace('\n',''),open(sys.argv[1],'r').readlines())
gen=map(lambda x:x.replace('\n',''),open(sys.argv[2],'r').readlines())

partitive=list()
for n in nom:
    for g in gen:
        print "Is this combination:"
        print "(p) most likely partitive"
        print "(s) most likely possesive"
        print "(x) the left word is unlikely partitive"
        print n , g
