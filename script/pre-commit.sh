#!/bin/bash

LOG_FILE="$MAIN_REPO_PATH/pre-commit-log.txt"
{
  # root directory
  MAIN_REPO_PATH=$(git rev-parse --show-toplevel)
  # submodule directory
  SUBMODULE_PATH="$MAIN_REPO_PATH/board-submodule"
  # 스테이징 영역에 있는 변경된 파일 불러오기
  TARGET_FILES=$(git diff --staged --name-only | grep 'application')
  echo "MAIN_REPO_PATH :: $MAIN_REPO_PATH"
  echo "SUBMODULE_PATH :: $SUBMODULE_PATH"

  echo "-----PRE-COMMIT START-----"
  for file in $TARGET_FILES; do
    if [[ "$file" == *application* ]]; then
      echo "read $file for"
      mkdir -p "$SUBMODULE_PATH/backend"
      cp "$MAIN_REPO_PATH/src/main/resources/$file" "$SUBMODULE_PATH/backend/$file"

      cd "$SUBMODULE_PATH"
      git add "backend/$file"
      echo "Pre-commit $file for submodule"
    fi
  done

  # commit & push in submodule repository
  echo "commit & push in submodule repository"
  git commit -m "Feat: update application files"
  git push origin main

  cd "$MAIN_REPO_PATH"
  echo "-----PRE-COMMIT END-----"
} 2>&1 | tee -a "$LOG_FILE"