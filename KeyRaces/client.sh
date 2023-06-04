#!/usr/bin/env bash

JAVA_HOME=~/.jdks/corretto-17.0.5
JAVAFX=~/.jdks/openjfx-17.0.7_linux-x64_bin-sdk/javafx-sdk-17.0.7

cp ./Client_module/target/client.jar ./jars

${JAVA_HOME}/bin/java \
	--module-path jars:${JAVAFX}/lib \
	-m com.teasgen.keyraces.client/com.teasgen.keyraces.client.Client
