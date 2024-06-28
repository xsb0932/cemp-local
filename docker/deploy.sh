#!/bin/sh

# 使用说明，用来提示输入参数
usage() {
	echo "Usage: sh 执行脚本.sh [start|stop|rm] <services>"
	exit 1
}

# 启动程序模块（必须）
start(){
  echo "Starting service: $2"
	docker-compose -f /docker/docker-compose-dev.yml up -d --build --force-recreate $2
}

# 关闭所有环境/模块
stop(){
  echo "Stopping service: $2"
	docker-compose stop $2
}

# 删除所有环境/模块
rm(){
  echo "Removing service: $2"
	docker-compose rm -f $2
}

# 根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
"start")
  shift
  start "$2"
;;
"stop")
  shift
  stop "$2"
;;
"rm")
  shift
  rm "$2"
;;
*)
	usage
;;
esac
