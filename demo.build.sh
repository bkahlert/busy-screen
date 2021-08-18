#!/bin/sh

BUILD_DIR="build/demo"

mkdir -p "${BUILD_DIR}"
cp demo.conf "${BUILD_DIR}"

cd "${BUILD_DIR}" || exit 1

docker run --rm -it \
           -v /var/run/docker.sock:/var/run/docker.sock \
           -v /tmp/kommons:/tmp/kommons \
           -v "$(pwd)":"$(pwd)" \
           -w "$(pwd)" \
           bkahlert/kustomize \
           --config-file demo.conf \
           --jaeger-hostname host.docker.internal
