#!/bin/sh
APP_HOME=$(dirname "$0")
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ -n "$JAVA_HOME" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA="java"
fi
exec "$JAVA" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
