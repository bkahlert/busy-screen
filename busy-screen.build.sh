#!/bin/sh

set -e

ORG_GRADLE_PROJECT_isProduction=true ./gradlew build -x test
BUILD_DIR="build/image"

rm -rf "${BUILD_DIR}"
mkdir -p "${BUILD_DIR}"
cp -r kustomize/ "${BUILD_DIR}"
cp -r build/distributions/ "${BUILD_DIR}/home/busy-screen/public_html"

cd "${BUILD_DIR}" || exit 1

docker run --rm -it \
           -v /var/run/docker.sock:/var/run/docker.sock \
           -v /tmp/kommons:/tmp/kommons \
           -v "$(pwd)":"$(pwd)" \
           -w "$(pwd)" \
           bkahlert/kustomize \
           --config-file busy-screen.conf \
           --jaeger-hostname host.docker.internal
