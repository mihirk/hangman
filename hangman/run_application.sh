#!/bin/sh
set -e -x
export LEIN_ROOT=True
exec "$@"
nohup lein ring server-headless >> hangman.log 2>&1 &