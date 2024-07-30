#!/bin/bash

# root directory
MAIN_REPO_PATH=$(git rev-parse --show-toplevel)
# submodule directory
SUBMODULE_PATH="$MAIN_REPO_PATH/board-submodule"
# 파일명이 application 인 파일 불러오기
TARGET_FILES=$(find "$MAIN_REPO_PATH/src/main/resources" -type f -name 'application*')

cd "$MAIN_REPO_PATH"
echo "-----------SUBMODULE COMMIT START-----------"
for file in $TARGET_FILES; do
  if [[ "$file" == *application* ]]; then
    echo "READ $file"
    cp "$file" "$SUBMODULE_PATH/backend"

    cd "$SUBMODULE_PATH"
    git add "backend"
  fi
done

# commit & push in submodule repository
git commit -m "Feat: update application files"
git push origin main
echo "-----------SUBMODULE COMMIT END-----------"
