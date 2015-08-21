ant clean

ant jar

cp apertium-viewer.jar ../../builds/apertium-viewer/
scp apertium-viewer.jar j:javabog.dk/filer/apertium/

svn commit ../../builds/apertium-viewer/


