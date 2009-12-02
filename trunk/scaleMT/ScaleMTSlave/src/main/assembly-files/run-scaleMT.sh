#!/bin/bash
 
export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games
export LANG=en_US.UTF-8

echo $$ >.pid

exec java -Djava.rmi.server.hostname="$1"  -jar ScaleMTSlave-1.0.jar -host $1 -RMIname ScaleMTSlave -RMIport 1331 $2
