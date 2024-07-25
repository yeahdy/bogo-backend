#!/bin/bash

# git pull from origin dev
git pull origin dev

# Iterate over each submodule and checkout main branch
git submodule foreach 'git checkout main'

# Run the gradle task
./gradlew copySubmodule

