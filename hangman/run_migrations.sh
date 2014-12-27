#!/bin/sh
set -e -x
export LEIN_ROOT=True
exec "$@"
lein ragtime migrate