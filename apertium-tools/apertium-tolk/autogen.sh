#!/bin/sh

echo 'python -c "import sys; sys.exit(0)"' > py-compile
chmod +x py-compile
intltoolize
aclocal
automake -a
autoconf
