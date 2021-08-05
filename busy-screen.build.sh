#!/bin/sh

./gradlew build -x test
BUILD_DIR="build/image"

mkdir -p "${BUILD_DIR}"
cp build/distributions/busy-screen.js "${BUILD_DIR}"
cp build/distributions/index.html "${BUILD_DIR}"
cp build/distributions/dialog-polyfill.css "${BUILD_DIR}"
cp build/distributions/dialog-polyfill.js "${BUILD_DIR}"
cp busy-screen.conf "${BUILD_DIR}"
cp busy-screen.flow "${BUILD_DIR}"
cp .env "${BUILD_DIR}"

cd "${BUILD_DIR}" || exit 1

docker run --rm -it \
           -v /var/run/docker.sock:/var/run/docker.sock \
           -v /tmp/koodies:/tmp/koodies \
           -v "$(pwd)":"$(pwd)" \
           -w "$(pwd)" \
           bkahlert/kustomize \
           --config-file busy-screen.conf \
           --jaeger-hostname host.docker.internal
