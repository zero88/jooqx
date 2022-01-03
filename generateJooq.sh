#!/usr/bin/env bash

SLEEP_TIME=${1:-30}
pg_dir=build/db/pg
pg_img=postgres:10-alpine

mysql_dir=build/db/mysql
mysql_img=mysql:8.0.23

docker rm -f postgres-gen mysql-gen

set -e

mkdir -p $pg_dir $mysql_dir
cp -rf sample/model/src/main/resources/pg_schema.sql $pg_dir
cp -rf sample/model/src/main/resources/mysql_schema.sql $mysql_dir

docker pull $pg_img
docker pull $mysql_img

docker run -d --name postgres-gen -p 5423:5432 \
        -v "$(pwd)/$pg_dir":/docker-entrypoint-initdb.d/ \
        -e POSTGRES_PASSWORD=123 -e POSTGRES_DB=testdb \
        $pg_img postgres

docker run -d --name mysql-gen -p 3360:3306 \
        -v "$(pwd)/$mysql_dir":/docker-entrypoint-initdb.d/ \
        -e MYSQL_ROOT_PASSWORD=123 -e MYSQL_DATABASE=testdb \
        $mysql_img --bind-address=0.0.0.0

sleep "$SLEEP_TIME"
./gradlew clean generateJooq

docker rm -f postgres-gen mysql-gen
