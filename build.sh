#!/bin/sh

# Global Variables
# EDIT AS REQUIRED
CLASS_DIR=WebContent/WEB-INF/classes
TOMCAT_LIB=/var/local/apache-tomcat-7.0.47/lib/*
PROJECT_LIB=WebContent/WEB-INF/lib/*
# DO NOT EDIT BEYOND THIS POINT

# Move into the base folder
cd webservice

# Setup all output directories
echo "setting up the output directory"
rm -rf $CLASS_DIR
mkdir $CLASS_DIR

# Compile the java into the above output directories
echo "compiling the project"
javac \
  -sourcepath src/ \
  -classpath $TOMCAT_LIB:$PROJECT_LIB:. \
  -d WebContent/WEB-INF/classes \
  src/uk/ac/hud/cryptic/servlet/Solver.java

# Create the WAR file
echo "producing the WAR file"
cd WebContent
jar -cf ../cryptic.war *
cd ..

echo "done"
