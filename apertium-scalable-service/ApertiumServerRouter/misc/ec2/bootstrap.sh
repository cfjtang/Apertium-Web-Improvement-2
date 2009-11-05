#! /bin/bash

CATALINA_HOME=/root/apache-tomcat
pushd /usr/bin
wget http://s3.amazonaws.com/ec2metadata/ec2-metadata
chmod a+x ec2-metadata
popd
unzip ApertiumServerRouter.war -d tmp
#cp AmazonOnDemandServer.properties tmp/WEB-INF/classes/
#cp OnDemandServerInterface.properties tmp/WEB-INF/classes/
cd tmp/WEB-INF/classes/
sed -i -e '/^requestrouter_rmi_host/d' configuration.properties
HOST=`ec2-metadata --public-hostname | cut -d ' ' -f 2`
sed -i -e "\$arequestrouter_rmi_host=$HOST" configuration.properties

sed -i -e '/^amazon_avzone/d' AmazonOnDemandServer.properties
AVZONE=`ec2-metadata --availability-zone | cut -d ' ' -f 2`
sed -i -e "\$aamazon_avzone=$AVZONE" AmazonOnDemandServer.properties

cd ../../
zip -r ApertiumServerRouter.war *
rm -Rf $CATALINA_HOME/webapps/ApertiumServerRouter
rm -Rf $CATALINA_HOME/webapps/ApertiumServerRouter.war
cp ApertiumServerRouter.war $CATALINA_HOME/webapps/
cd $CATALINA_HOME/bin
rmiregistry 1098&
./catalina.sh start
