#!/bin/sh

intltoolize && autoreconf -fi && ./configure "$@"
