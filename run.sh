#!/bin/bash

WORK_DIR=/data/file/kongyu/dev

set -x

# 拉取代码
git clone git@github.com:kongyu666/Ateng-Java.git ${WORK_DIR}/tmp/Ateng-Java
git clone git@github.com:kongyu666/Ateng-Boot.git ${WORK_DIR}/tmp/Ateng-Boot
git clone git@github.com:kongyu666/Ateng-Cloud.git ${WORK_DIR}/tmp/Ateng-Cloud

# 删除旧代码
rm -rf ${WORK_DIR}/work
mv ${WORK_DIR}/tmp ${WORK_DIR}/work

# 删除.git
rm -rf ${WORK_DIR}/work/*/.git/

# 上传代码
git add -v ${WORK_DIR}
git commit -m "修改文档"
git push
