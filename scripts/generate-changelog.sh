#!/bin/bash
set -e
LAST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "")
if [ -z "$LAST_TAG" ]; then
  RANGE="--pretty=format:'* %s (%h)'"
else
  RANGE="$LAST_TAG..HEAD"
fi
git log $RANGE --pretty=format:'* %s (%h)' > CHANGELOG.md
echo "CHANGELOG.md generated"

