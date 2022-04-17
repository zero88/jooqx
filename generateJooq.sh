#!/usr/bin/env bash

SLEEP_TIME=${1:-30}

mysql_dir=build/db/mysql
mysql_img=mysql:8.0.23

docker rm -f postgres-gen mysql-gen

set -e

mkdir -p $mysql_dir
cp -rf sample/model/src/main/resources/mysql_schema.sql $mysql_dir

docker pull $mysql_img

docker run -d --name mysql-gen -p 3360:3306 \
        -v "$(pwd)/$mysql_dir":/docker-entrypoint-initdb.d/ \
        -e MYSQL_ROOT_PASSWORD=123 -e MYSQL_DATABASE=testdb \
        $mysql_img --bind-address=0.0.0.0

sleep "$SLEEP_TIME"
./gradlew clean generateJooq

docker rm -f mysql-gen
