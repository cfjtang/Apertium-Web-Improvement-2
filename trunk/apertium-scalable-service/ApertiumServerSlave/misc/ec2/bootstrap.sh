APERTIUM_PATH=/root/local/bin
APERTIUM_SUP_PAIRS=ro-es,pt-gl,pt-es,pt-ca,oc-es,oc-ca,oc_aran-es,oc_aran-ca,gl-pt,gl-es,gl-en,fr-es,fr-ca,eu-es,es-ro,es-pt,es-pt_BR,es-oc,es-oc_aran,es-gl,es-fr,es-eo,es-en_US,es-en,es-ca,en-gl,en-es,en-eo,en-ca,cy-en,ca-pt,ca-oc,ca-oc_aran,ca-fr,ca-es,ca-eo,ca-en,br-fr
cp ApertiumServerSlave-1.0-assembled.zip /root
cp capacity.properties /root
pushd /usr/bin
wget http://s3.amazonaws.com/ec2metadata/ec2-metadata
chmod a+x ec2-metadata
popd
cd /root
unzip ApertiumServerSlave-1.0-assembled.zip
cd ApertiumServerSlave-1.0/conf
cp ../../capacity.properties ./
sed -i -e '/^apertium_path/d' configuration.properties
sed -i -e "\$aapertium_path=$APERTIUM_PATH" configuration.properties
sed -i -e '/^apertium_supported_pairs/d' configuration.properties
sed -i -e "\$aapertium_supported_pairs=$APERTIUM_SUP_PAIRS" configuration.properties
cd ..
./run-apertium-server-ec2.sh