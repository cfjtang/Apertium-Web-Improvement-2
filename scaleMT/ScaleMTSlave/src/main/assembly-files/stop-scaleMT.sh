#!/bin/bash

DIR=`dirname $0`
kill -9 `cat $DIR/.pid`
rm  $DIR/.pid