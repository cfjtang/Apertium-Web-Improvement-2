#!/bin/sh

echo 'python -c "import sys; sys.exit(0)"' > py-compile
chmod +x py-compile
intltoolize --copy --force --automake
aclocal
automake -a
autoconf
