@echo off

CHCP 65001
set MAVEN_OPTS=-Dfile.encoding=utf-8

set benchmark=%1
if [%1]==[] set benchmark=SimpleBenchmark

echo Run benchmark %benchmark%

mvn clean package exec:java -q -Dbenchmark=%benchmark%
