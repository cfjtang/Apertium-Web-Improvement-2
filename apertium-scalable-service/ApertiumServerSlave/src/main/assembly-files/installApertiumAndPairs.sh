#! /bin/bash
 
# APERTIUM INSTALLATION SCRIPT
#
# PARAMETERS:
#
# -p INSTALLATION_PREFIX
# Root directory for the bin, share and lib where Apertium will be installed. Default is ~/local
#
# -c APERTIUM_SERVER_CONFIGURATION_FILE
# Properties file that contains application configuration parameters. Default is ./lib/configuration.properties. Generally it
# is not necessary to edit this property
#
# -l LANGUAGEPAIR1 [,LANGUAGEPAIR2 ...]
# Language pairs to be installed, as they are named in svn repository, separated by commas. For example, es-ca,en-es will work, but not ca-es,en-es because
# svn directory is apertium-es-ca not apertium-ca-es. Default is the list of stable pairs.
# 
#

message()
{
	echo "Error. Wrong parameters."
	echo "Usage: $0  [-p Installation_Prefix] [-c ApertiumServer_configuration_file] [-l language_pairs]"

}

process()
{

PREFIX=$1
CONF_FILE=$2

DIRS_CHECKOUT="apertium lttoolbox "

for pair in $3; do
	DIRS_CHECKOUT="$DIRS_CHECKOUT apertium-$pair"
done


for dir in $DIRS_CHECKOUT; do
echo "downloading $dir ..."
if [ -d ./apertium/$dir/.svn ]
then
	cd apertium/$dir
	svn up
	cd ../..
else
	svn co http://apertium.svn.sourceforge.net/svnroot/apertium/trunk/$dir apertium/$dir
fi

done

#VISL CG3
echo "downloading VISL CG3 ..."
if [ -d ./apertium/vislcg3/.svn ]
then
	cd apertium/vislcg3
	svn up #-R 4854
	cd ../..
else
	svn co --username anonymous --password anonymous http://beta.visl.sdu.dk/svn/visl/tools/vislcg3/trunk apertium/vislcg3 #-r 4854
fi
#Patch
#cd apertium/vislcg3
#patch -p0 -i ../../vislcg3-4854-nullflush.diff
#cd ../..

echo "Prefix: $PREFIX"

REAL_PREFIX=$PREFIX
if [ -z "$REAL_PREFIX" ]
then
	REAL_PREFIX=/usr/local
fi

mkdir -p $REAL_PREFIX
mkdir -p $DICT_DIR

echo "Real prefix: $REAL_PREFIX"

cd apertium

#install vislcg3
echo "Installing vislcg3"
cd vislcg3
sh autogen.sh --prefix=$PREFIX
make
make install
cd ..

#install lt-toolbox
echo "Installing lt-toolbox"
cd lttoolbox
PKG_CONFIG_PATH=$REAL_PREFIX/lib/pkgconfig sh autogen.sh --prefix=$PREFIX
make
make install
cd ..

#install apertium
echo "Installing apertium"
cd apertium
PKG_CONFIG_PATH=$REAL_PREFIX/lib/pkgconfig sh autogen.sh --prefix=$PREFIX
make
make install
cd ..


#install pairs
PATH=$REAL_PREFIX/bin:$PATH
for pair in $3
do
	list=`ls -d apertium-$pair`
	for folder in $list 
	do
		echo "Installing $folder"
		cd $folder

		#install in fake folder to know which mode files are generated
		mkdir -p /tmp/apertiumServerGSOC/$folder/share/apertium/modes
		PKG_CONFIG_PATH=$REAL_PREFIX/lib/pkgconfig sh autogen.sh --prefix=/tmp/apertiumServerGSOC/$folder
	 	make 
		make install
		pushd /tmp/apertiumServerGSOC/$folder/share/apertium/modes
		installed_modes=`ls | grep -E  ".mode$"`
		popd
		rm -Rf /tmp/apertiumServerGSOC/$folder

		#Change modes.xml
		mv modes.xml modes.xml.original
		if [ "enable-yes" = $4 ]
		then
			java -jar ../../ApertiumServerSlave-1.0.jar -processModes -inputModes modes.xml.original -outputModes modes.xml -tradubiDictionaryPath $5 -prefix $REAL_PREFIX/bin
		else
			java -jar ../../ApertiumServerSlave-1.0.jar -processModes -inputModes modes.xml.original -outputModes modes.xml
		fi
		#install in real folder
		PKG_CONFIG_PATH=$REAL_PREFIX/lib/pkgconfig sh autogen.sh --prefix=$PREFIX
		make 
		make install
		mv modes.xml modes.xml.modified
		mv modes.xml.original modes.xml
		for installed_mode in $installed_modes
		do
			cp ${installed_mode/%.mode/}-null.mode $REAL_PREFIX/share/apertium/modes
		done

		cd ..
	done
done
	
cd ..

#Add successfully installed pairs to ApertiumServer configuration file

#delete apertium_supported_pairs line
cp $CONF_FILE $CONF_FILE.old
sed -i -e '/^apertium_supported_pairs/d' $CONF_FILE
sed -i -e '$aapertium_supported_pairs=' $CONF_FILE

pushd $REAL_PREFIX/share/apertium/modes
list=`ls | grep -E  "\-null.mode$"` # list of modes
for file in $list 
do
	popd
	sed -i -e "s!apertium_supported_pairs=!&${file/%-null.mode/},!" $CONF_FILE #${file:0:5}
	pushd $REAL_PREFIX/share/apertium/modes
done
popd

#Add Apertium executable path to ApertiumServer configuration file
sed -i -e '/^apertium_path/d' $CONF_FILE
sed -i -e "\$aapertium_path=$REAL_PREFIX/bin/" $CONF_FILE

#remove comma
sed -i -e 's/,$//' $CONF_FILE

#Copy additional files
cp toinstall/execAndGetPID.sh toinstall/getProcessInfo.sh $REAL_PREFIX/bin

if [ "enable-yes" = $4 ]
 then
cp apertium-module/* $REAL_PREFIX/bin
fi

}

PREFIX=~/local
CONF_FILE=./conf/configuration.properties
LANGUAGE_PAIRS="es-ca es-ro fr-ca oc-ca en-gl oc-es es-pt en-ca en-es eo-en es-gl fr-es eo-es cy-en eo-ca pt-ca pt-gl eu-es br-fr"
DICT_DIR=/tmp/apertium_dictionaries

while getopts ":p:c:d:l:t" optname
  do
    case "$optname" in
      "p")
        PREFIX=$OPTARG
        ;;
      "c")
        CONF_FILE=$OPTARG
        ;;
      "d")
        DICT_DIR=$OPTARG
        ;;
      "l")
        LANGUAGE_PAIRS=`echo "$OPTARG" | sed -r 's/,/ /g'`
        ;;
	"t")
        TRADUBI_ENABLED=yes
        ;;
      "?")
        echo "Unknown option $OPTARG"
	message
	exit
        ;;
      ":")
        echo "No argument value for option $OPTARG"
	message
	exit
        ;;
      *)
      # Should not occur
        echo "Unknown error while processing options"
        ;;
    esac
  done

echo "=== APERTIUM INSTALLATION SCRIPT ==="
echo "Installation prefix (-p option): $PREFIX"
echo "ApertiumServerSlave configuration file (-c option): $CONF_FILE"
echo "Language pairs to be installed (-l option): $LANGUAGE_PAIRS"

if [ $TRADUBI_ENABLED ]
then
	echo "Tradubi enabled. Dictionary directory (-d option): $DICT_DIR"
fi

process $PREFIX $CONF_FILE "$LANGUAGE_PAIRS" "enable-$TRADUBI_ENABLED" $DICT_DIR

