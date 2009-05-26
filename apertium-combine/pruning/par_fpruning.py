#!/opt/local/bin/python2.5

import thread, threading, time, sys, subprocess
import fpruning

ncpu = 4
chunkname = sys.argv[1]
tmp = open('tmp','rw')
lsgrep = subprocess.call('ls | grep '+chunkname, shell=True, stdout=tmp)
to_prune = []
for l in tmp:
    to_prune.append(l.strip())
tmp.close()
print to_prune

class ConsumerThread(threading.Thread):
    def run(self):
        print "consumer runs"

class ProviderThread(threading.Thread):
    def run(self):
        print "provider runs"
        for x in xrange (ncpu):
            ConsumerThread().start()

ProviderThread().start()

