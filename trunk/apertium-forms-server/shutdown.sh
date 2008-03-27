#!/bin/sh

PID=`ps a | grep forms-server | head -1 | awk '{print $1}'`
echo $PID
kill -9 $PID
