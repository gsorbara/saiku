
echo "Building saiku-core"
cd saiku-core
call mvn clean install
cd ..
echo "Building saiku-webapp"
cd saiku-webapp
call mvn clean install
cd ..
call git submodule init
call git submodule update
echo "Building saiku-ui"
cd saiku-ui
call git checkout 2.4
call git pull
call mvn clean package install:install-file -Dfile=target/saiku-ui-2.4.war  -DgroupId=org.saiku -DartifactId=saiku-ui -Dversion=2.4 -Dpackaging=war
echo "Building saiku-server"
cd ..\saiku-server
call mvn clean package
echo "Building saiku-bi-platform-plugin-util"
cd ..\saiku-bi-platform-plugin-util
call mvn clean install
echo "Building saiku-bi-platform-plugin"
cd ..\saiku-bi-platform-plugin
call mvn clean package
