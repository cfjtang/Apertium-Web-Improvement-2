#!/bin/sh

PID=`ps xa | grep forms-server | head -1 | awk '{print $1}'`
echo $PID
kill -9 $PID
