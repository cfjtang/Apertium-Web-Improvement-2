install_dir=/usr/local

binaries=desman desmnemo reman remnemo
shells=apertium-man apertium-mnemo


all:	$(binaries)


install: $(binaries) $(shells)
	cp -p  $(binaries) $(shells) $(install_dir)/bin
	find man | cpio -pdmv $(install_dir)


desman:	desman.c
	cc -o desman desman.c

desmnemo: desmnemo.c
	cc -o desmnemo desmnemo.c

reman:	generic-reformatter.c
	cc -o reman generic-reformatter.c

remnemo: reman
	cp reman remnemo
