#!/bin/bash
 
export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games
#export LANG="es_ES.utf8"
#export LC_ALL="es_ES.utf8"

echo $$ >.pid

exec java -Djava.rmi.server.hostname="$1"  -jar `dirname $0`/ScaleMTSlave-1.0.jar -host $1 -RMIname ScaleMTSlave -RMIport 1331 $2
