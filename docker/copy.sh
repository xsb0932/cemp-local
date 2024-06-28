#!/bin/sh

# 复制项目的文件到对应docker路径，便于一键生成镜像。
usage() {
	echo "Usage: sh copy.sh <jar_prefixes>"
  echo "Example: sh copy.sh cemp-gateway,cemp-oauth/cemp-oauth-biz"
	exit 1
}

# 定义函数用于复制 JAR 包
copy_jar() {
    source_path=$1  # 源文件路径
    target_path=$2  # 目标文件路径

    echo "Begin copy $target_path"
    cp $source_path $target_path
}

# 检查输入参数是否为空
if [ -z "$1" ]; then
    echo "Error: Please provide jar prefixes."
    usage
fi

# 按逗号分隔输入参数，并循环处理每个前缀名
IFS=',' read -ra jar_prefixes <<< "$1"
for prefix in "${jar_prefixes[@]}"; do
    case $prefix in
        cemp-gateway)
            copy_jar "../cemp-gateway/target/cemp-gateway.jar" "./cemp-gateway/cemp-gateway.jar"
            ;;
        cemp-oauth/cemp-oauth-biz)
            copy_jar "../cemp-oauth/cemp-oauth-biz/target/cemp-oauth-biz.jar" "./cemp-oauth/cemp-oauth-biz.jar"
            ;;
        cemp-gw-jinjiang)
            copy_jar "../cemp-gw-jinjiang/target/cemp-gw-jinjiang.jar" "./cemp-gw-jinjiang/cemp-gw-jinjiang.jar"
            ;;
        cemp-gw-lgc)
            copy_jar "../cemp-gw-lgc/target/cemp-gw-lgc.jar" "./cemp-gw-lgc/cemp-gw-lgc.jar"
            ;;
        cemp-gw)
            copy_jar "../cemp-gw/target/cemp-gw.jar" "./cemp-gw/cemp-gw.jar"
            ;;
        cemp-data/cemp-data-biz)
            copy_jar "../cemp-data/cemp-data-biz/target/cemp-data-biz.jar" "./cemp-data/cemp-data-biz.jar"
            ;;
        cemp-monitor/cemp-monitor-biz)
            copy_jar "../cemp-monitor/cemp-monitor-biz/target/cemp-monitor-biz.jar" "./cemp-monitor/cemp-monitor-biz.jar"
            ;;
        cemp-bms/cemp-bms-biz)
            copy_jar "../cemp-bms/cemp-bms-biz/target/cemp-bms-biz.jar" "./cemp-bms/cemp-bms-biz.jar"
            ;;
        cemp-lgc/cemp-lgc-biz)
            copy_jar "../cemp-lgc/cemp-lgc-biz/target/cemp-lgc-biz.jar" "./cemp-lgc/cemp-lgc-biz.jar"
            ;;
        cemp-file/cemp-file-biz)
            copy_jar "../cemp-file/cemp-file-biz/target/cemp-file-biz.jar" "./cemp-file/cemp-file-biz.jar"
            ;;
        cemp-energy/cemp-energy-biz)
            copy_jar "../cemp-energy/cemp-energy-biz/target/cemp-energy-biz.jar" "./cemp-energy/cemp-energy-biz.jar"
            ;;
        cemp-job/cemp-job-biz)
            copy_jar "../cemp-job/cemp-job-biz/target/cemp-job-biz.jar" "./cemp-job/cemp-job-biz.jar"
            ;;
        cemp-cus-jjgj)
            copy_jar "../cemp-cus-jjgj/target/cemp-cus-jjgj.jar" "./cemp-cus-jjgj/cemp-cus-jjgj.jar"
            ;;
        cemp-cus-sdl)
            copy_jar "../cemp-cus-sdl/target/cemp-cus-sdl.jar" "./cemp-cus-sdl/cemp-cus-sdl.jar"
            ;;
        cemp-cus-jzd)
            copy_jar "../cemp-cus-jzd/target/cemp-cus-jzd.jar" "./cemp-cus-jzd/cemp-cus-jzd.jar"
            ;;
        cemp-cus-lh)
            copy_jar "../cemp-cus-lh/target/cemp-cus-lh.jar" "./cemp-cus-lh/cemp-cus-lh.jar"
            ;;
        # 添加其他前缀名的处理
        # prefix1)
        #     copy_jar "source_path1" "target_path1"
        #     ;;
        *)
            echo "Error: Invalid jar prefix: $prefix"
            ;;
    esac
done
