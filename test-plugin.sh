#!/bin/bash

./gradlew build pTML
./gradlew -b build.templar.gradle templarGen 
