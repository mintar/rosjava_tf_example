#!/bin/sh

BASEDIR=$(dirname $0)
WRAPPER=$BASEDIR/../build/install/rosjava_tf_example/bin/rosjava_tf_example

if [ ! -x $WRAPPER ]; then
  echo "ERROR: $WRAPPER does not exist. Run gradle installApp first."
  exit 1
fi

$WRAPPER rosjava_tf_example.TransformListener

