#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import xmlrpclib, sys, os, re
from threading import Thread

class testit(Thread):
	def __init__ (self, proxy):
		Thread.__init__(self)
		self.proxy = proxy
		
	def run(self):
		for c in range(1, 1024):
			self.proxy.translate('Hovuddrag i norsk språkhistorie', 'nb', 'nn');

def main():
	proxy = xmlrpclib.ServerProxy("http://localhost:6173/RPC2");
	for c in range(1, 8):
		cur = testit(proxy)
		cur.start()

if __name__ == "__main__":
	main()
