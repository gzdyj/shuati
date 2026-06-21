#!/bin/bash
set -e

mysql -uroot -p"$MYSQL_ROOT_PASSWORD" --default-character-set=utf8mb4 <<'EOF'
SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS quiz_master DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quiz_master;
SET NAMES utf8mb4;
EOF

mysql -uroot -p"$MYSQL_ROOT_PASSWORD" --default-character-set=utf8mb4 quiz_master < /docker-entrypoint-initdb.d/schema.sql
