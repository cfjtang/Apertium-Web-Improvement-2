#! /bin/sh

aclocal \
&& autoconf \
&& automake --add-missing --gnu \
&& ./configure "$@"
