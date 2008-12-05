rm -f apertium-viewer.war
cd dist
zip -r ../apertium-viewer.war .
cd ..
firefox http://javabog.dk:8080/manager/html/undeploy?path=/apertium-viewer  &
firefox http://javabog.dk:8080/manager/html

