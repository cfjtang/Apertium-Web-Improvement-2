#!/bin/bash

#export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games
#export LANG=en_US.UTF-8

echo $$ >.pid

#java -Djava.rmi.server.hostname="`ec2-metadata --public-hostname | cut -d ' ' -f 2`"  -jar ApertiumServer.jar
exec java -Djava.rmi.server.hostname="`ec2-metadata --public-hostname | cut -d ' ' -f 2`"  -jar ScaleMTSlave-1.0.jar -host `ec2-metadata --public-hostname | cut -d ' ' -f 2` -RMIname ApertiumServerGSOC -RMIport 1331 -routerHost `ec2-metadata --user-data | cut -d ' ' -f 2`
