@echo off

set benchmark=%1
if [%1]==[] set benchmark=SimpleBenchmark

echo Run benchmark %benchmark%

mvn clean package exec:java -q -Dbenchmark=%benchmark%
