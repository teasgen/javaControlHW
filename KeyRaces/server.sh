#!/usr/bin/env bash

JAVA_HOME=~/.jdks/corretto-17.0.5

cp ./Server_module/target/server.jar ./jars

${JAVA_HOME}/bin/java \
	-p ./jars \
	-m com.teasgen.keyraces.server/com.teasgen.keyraces.server.GroupServer
