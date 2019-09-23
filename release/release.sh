#!/bin/sh

set -e
set -x

echo "Building Backend Image..."
cd ../server \
  && ./gradlew jibDockerBuild \
  && cd ../release
echo "Backend Image built"

echo "Building Frontend Image..."
cd ../gui \
  && npm pack \
  && cd ../release
echo "Frontend Image built"
