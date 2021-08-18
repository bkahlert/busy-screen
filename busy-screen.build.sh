#!/bin/sh

set -e

env ORG_GRADLE_PROJECT_isProduction=true ./gradlew build -x test
BUILD_DIR="build/image"

mkdir -p "${BUILD_DIR}" \
 && cp busy-screen.conf "${BUILD_DIR}" \
 && cp .env "${BUILD_DIR}" \
 && cp -r build/distributions/ "${BUILD_DIR}/public_html"

mkdir -p "${BUILD_DIR}/node-red" \
 && cp busy-screen.flow "${BUILD_DIR}/node-red/flows.json"

cd "${BUILD_DIR}" || exit 1

docker run --rm -it \
           -v /var/run/docker.sock:/var/run/docker.sock \
           -v /tmp/kommons:/tmp/kommons \
           -v "$(pwd)":"$(pwd)" \
           -w "$(pwd)" \
           bkahlert/kustomize \
           --config-file busy-screen.conf \
           --jaeger-hostname host.docker.internal
