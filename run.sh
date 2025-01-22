#!/bin/bash

WORK_DIR=/data/file/kongyu/dev

# 输入参数
SOURCE=$1

if [ -z "${SOURCE}" ]
then
    echo "usge: $0 Ateng-Java"
    exit 1
fi

set -x

# 拉取代码
git clone git@github.com:kongyu666/${SOURCE}.git ${WORK_DIR}/tmp/${SOURCE}

# 删除旧代码
rm -rf ${WORK_DIR}/work
mv ${WORK_DIR}/tmp ${WORK_DIR}/work

# 删除.git
rm -rf ${WORK_DIR}/work/${SOURCE}/.git/

# 上传代码
git add -v ${WORK_DIR}
git commit -m "修改文档"
git push
