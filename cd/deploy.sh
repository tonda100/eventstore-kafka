#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'dev' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    mvn deploy -X -P sign,build-extras --settings cd/mvnsettings.xml
fi