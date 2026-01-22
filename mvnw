#!/usr/bin/env sh
# ----------------------------------------------------------------------------
# Maven Wrapper startup script, version 3.3.2
# ----------------------------------------------------------------------------

set -euf

BASE_DIR=$(cd "$(dirname "$0")" && pwd)
WRAPPER_DIR="$BASE_DIR/.mvn/wrapper"
PROPS_FILE="$WRAPPER_DIR/maven-wrapper.properties"
JAR_FILE="$WRAPPER_DIR/maven-wrapper.jar"

if [ ! -f "$PROPS_FILE" ]; then
  echo "Missing $PROPS_FILE" >&2
  exit 1
fi

get_prop() {
  key="$1"
  # shellcheck disable=SC2002
  cat "$PROPS_FILE" | grep "^${key}=" | tail -n 1 | cut -d'=' -f2-
}

WRAPPER_URL=$(get_prop "wrapperUrl")
DIST_URL=$(get_prop "distributionUrl")

if [ ! -f "$JAR_FILE" ]; then
  echo "Downloading Maven Wrapper jar..." >&2
  mkdir -p "$WRAPPER_DIR"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$WRAPPER_URL" -o "$JAR_FILE"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$WRAPPER_URL" -O "$JAR_FILE"
  else
    echo "Neither curl nor wget is available to download $WRAPPER_URL" >&2
    exit 1
  fi
fi

exec java -Dmaven.multiModuleProjectDirectory="$BASE_DIR" \
  -classpath "$JAR_FILE" \
  org.apache.maven.wrapper.MavenWrapperMain \
  -Dmaven.wrapper.distributionUrl="$DIST_URL" \
  "$@"


