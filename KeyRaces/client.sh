#!/usr/bin/env bash

JAVA_HOME=~/.jdks/corretto-17.0.5

cp ./Client_module/target/client.jar ./jars

${JAVA_HOME}/bin/java \
	-p ./jars \
	-m com.teasgen.keyraces.client/com.teasgen.keyraces.client.Client
