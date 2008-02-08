#!/bin/sh

intltoolize
aclocal
automake -a
autoconf
