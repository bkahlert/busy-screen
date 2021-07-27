#!/bin/sh

BUILD_DIR="build/demo"

mkdir -p "${BUILD_DIR}"
cp demo.conf "${BUILD_DIR}"

cd "${BUILD_DIR}" || exit 1

docker run \
  --rm -it \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /tmp/koodies:/tmp/koodies \
  -v "$(pwd)":"$(pwd)" \
  -w "$(pwd)" \
  -e TERM="$TERM" \
  -e TERM_PROGRAM="$TERM_PROGRAM" \
  -e COLORTERM="$COLORTERM" \
  imgcstmzr \
  --config-file demo.conf
