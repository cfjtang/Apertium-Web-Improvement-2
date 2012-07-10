#! /bin/sh

xsltproc getKinds.xsl $1 | sort | uniq
