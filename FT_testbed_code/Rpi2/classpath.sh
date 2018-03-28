#!/bin/sh
export JADELIB=~/Desktop/jade/lib
echo Running export JADELIB=~/Desktop/jade/lib
export CLASSPATH=$JADELIB/jade.jar:$JADELIB/commons-codec/commons-codec-1.3.jar:.
echo Running export CLASSPATH=$JADELIB/jade.jar:$JADELIB/commons-codec/commons-codec-1.3.jar:.
echo Environment variables set
echo Classpath is $CLASSPATH

#set CLASSPATH=%JADE_LIB%\jade.jar;%JADE_LIB%\commons-codec\commons-codec-1.3.jar;.