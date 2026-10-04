#!/bin/sh

#
# Copyright © 2015-2024 the original author or authors.
# Licensed under Apache-2.0
#

DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"

APP_HOME=$( cd "$( dirname "$0" )" && pwd )
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

warn () { echo "$*"; }
die () { echo; echo "$*"; echo; exit 1; }

cygwin=false; msys=false; darwin=false; nonstop=false
case "$(uname)" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true ;;
  MINGW*|MSYS*) msys=true ;;
esac

CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -n "$JAVA_HOME" ]; then
  if [ -x "$JAVA_HOME/jre/sh/java" ]; then
    JAVACMD="$JAVA_HOME/jre/sh/java"
  else
    JAVACMD="$JAVA_HOME/bin/java"
  fi
  [ -x "$JAVACMD" ] || die "ERROR: JAVA_HOME invalid: $JAVA_HOME"
else
  JAVACMD="java"
  which java >/dev/null 2>&1 || die "ERROR: no 'java' found in PATH"
fi

set -- \
  $DEFAULT_JVM_OPTS \
  $JAVA_OPTS \
  $GRADLE_OPTS \
  "-Dorg.gradle.appname=$APP_BASE_NAME" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"

exec "$JAVACMD" "$@"
