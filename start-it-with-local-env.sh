#!/bin/sh

set -e

help() {
   echo "Starts the integration tests with a local ZAC Docker Image."
   echo
   echo "Syntax: $0 [-l|d|v|u|h]"
   echo "options:"
   echo "-l     Build a local ZAC Docker image"
   echo "-d     Delete local Docker volume data before starting Docker Compose."
   echo "-v     Keep local Docker Compose volume data after test execution"
   echo "-u     Turn on debug logs"
   echo "-h     Print this Help."
   echo
}

echoerr() {
  echo 1>&2;
  echo "$@" 1>&2;
  echo 1>&2;
}

volumeDataFolder="./scripts/docker-compose/volume-data"
args="--rerun-tasks"

[ -f fix-permissions.sh ] && ./fix-permissions.sh

build=false
while getopts ':ldvuh' OPTION; do
  case "$OPTION" in
    l)
      build=true
      ;;
    d)
      echo "Deleting local Docker volume data folder: '$volumeDataFolder'.."
      rm -rf $volumeDataFolder
      echo "Done"
      ;;
    v)
      export REMOVE_DOCKER_COMPOSE_VOLUMES=false
      ;;
    u)
      echo "Turning on debug logs ..."
      args="$args -Si -Dorg.gradle.vfs.watch=true"
      ;;
    h)
      help
      exit;;
    \?)
      echoerr "Error: Invalid option $OPTION"
      help
      exit;;
  esac
done

if [ $build = "true" ]; then
  echo "Building ZAC Docker Image ..."
  ./gradlew buildDockerImage
fi

export ZAC_DOCKER_IMAGE=ghcr.io/infonl/zaakafhandelcomponent:dev
[ -f check-env.sh ] && ./check-env.sh

# shellcheck disable=SC2086
./gradlew $args itest
