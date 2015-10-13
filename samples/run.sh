#!/bin/bash
java -agentpath:./liblagent.so=interval=7,logPath=log-`date +%s`.hpl -cp /honest-profiler/samples PerformanceTest
