#!/bin/bash

set -e
if [ -z "$1"]; then
  echo "Usage: ./git-create-feature.sh <short-name>"
  exit 1
fi

BR=feature/$1
git checkout dev
git pull origin dev
git checkout -b $BR
echo "Created branch $BR"
