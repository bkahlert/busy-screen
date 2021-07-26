#!/bin/sh
cd build || exit 1
mkdir -p "image/cache"
cd image || exit 1
cp ../../demo.conf .
docker run \
  --rm -it \
  --mount type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock \
  --mount type=bind,source=/tmp/koodies,target=/tmp/koodies \
  --mount type=bind,source="$(pwd)",target="$(pwd)" \
  --mount type=bind,source="${HOME}/.env.imgcstmzr",target="$(pwd)/env" \
  -w "$(pwd)" \
  -e TERM="$TERM" \
  -e TERM_PROGRAM="$TERM_PROGRAM" \
  -e COLORTERM="$COLORTERM" \
  imgcstmzr \
  --cache-dir /work/cache \
  --config demo.conf \
  --env-file env
