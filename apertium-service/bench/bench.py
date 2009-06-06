#!/usr/bin/python

import time
import xmlrpclib

#
def timing(func):
    def wrapper(*arg):
        t1 = time.time()
        res = func(*arg)
        t2 = time.time()
        print '%s took %0.3f ms' % (func.func_name, (t2-t1)*1000.0)
        return res
    return wrapper

@timing
def bench():
    proxy = xmlrpclib.ServerProxy("http://localhost:1234/ApertiumServer")
    for i in range(1, 1000):
        proxy.translate("This is a test for the machine translation program", "en-es")
    
if __name__ == "__main__":
    bench()

