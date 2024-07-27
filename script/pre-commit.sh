#!/bin/bash

# root directory
MAIN_REPO_PATH=$(git rev-parse --show-toplevel)
# submodule directory
SUBMODULE_PATH="$MAIN_REPO_PATH/board-submodule"
# 스테이징 영역에 있는 변경된 파일 불러오기
TARGET_FILES=$(git diff --staged --name-only | grep 'application')

for file in $TARGET_FILES; do
  if [[ "$file" == *application* ]]; then
    cp "$MAIN_REPO_PATH/src/main/resources/$file" "$SUBMODULE_PATH/backend/$file"

    cd "$SUBMODULE_PATH"
    git add "backend/$file"
    echo "Pre-commit $file for submodule"
  fi
done

# commit & push in submodule repository
git commit -m "Feat: update application files"
git push origin main

cd "$MAIN_REPO_PATH"
