#!/bin/bash

# 根据Dockerfile打包镜像并启动容器
start() {
    local BIZ_ID="$1"  # 网关业务id
    local LOWER_BIZ_ID=$(echo "$1" | tr '[:upper:]' '[:lower:]')
#    local IMAGE_NAME="cemp-gw-${LOWER_BIZ_ID}:latest" # 镜像名称(只能小写)
    local CONTAINER_NAME="cemp-gw-${LOWER_BIZ_ID}" # 容器名称(只能小写)

    # 构建镜像
#    docker buildx build --build-arg BIZ_ID="${BIZ_ID}" -t "${IMAGE_NAME}" /docker/cemp-gw

    # 启动容器
    docker run -d -v /docker/cemp-gw/"${BIZ_ID}".json:/app/"${BIZ_ID}".json -e BIZ_ID="${BIZ_ID}" -e cemp.gateway-biz-id="${BIZ_ID}" -e mqtt.client-id="cemp-gw-${BIZ_ID}" -e SPRING_PROFILES_ACTIVE=dev -e SPRING_CLOUD_NACOS_DISCOVERY_IP=172.20.1.94 --name "${CONTAINER_NAME}" --log-driver json-file --log-opt max-size=3g cemp-gw:latest

    if [ $? -eq 0 ]; then
        echo 'start success'
        exit 0
    else
        echo 'start failed'
        exit 1
    fi
}

# 停止容器
stop() {
    local BIZ_ID="$1"  # 网关业务id
    local LOWER_BIZ_ID=$(echo "$1" | tr '[:upper:]' '[:lower:]')
    local CONTAINER_NAME="cemp-gw-${LOWER_BIZ_ID}" # 容器名称

    # 停止容器
    docker stop "${CONTAINER_NAME}" && docker rm "${CONTAINER_NAME}"

    if [ $? -eq 0 ]; then
        echo 'stop success'
        exit 0
    else
        echo 'stop failed'
        exit 1
    fi
}

# 根据传入参数调用不同的方法
case "$1" in
    "start")
        start "$2"
        ;;
    "stop")
        stop "$2"
        ;;
    *)
        echo "Invalid option"
        exit 1
        ;;
esac