#! /bin/bash

while(true)
do
	sleep 1
	echo -e "$(date) \t $(pmap  $1 -x | grep total | sed -E 's/ +/ /g' | cut -f 4 -d ' ')"  ### >> memoryUsageBaseXServer.txt 
done
