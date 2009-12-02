#!/bin/bash
 
export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games

export LANG="es_ES.utf8"
export LC_ALL="es_ES.utf8"


echo $$ >.pid

#java -Djava.rmi.server.hostname="`ec2-metadata --public-hostname | cut -d ' ' -f 2`"  -jar ApertiumServer.jar
exec java -Djava.rmi.server.hostname="$1"  -jar ApertiumServerSlave-1.0.jar -host $1 -RMIname ApertiumServerGSOC -RMIport 1331 $2
