#!/bin/sh

# 复制项目的文件到对应docker路径，便于一键生成镜像。
usage() {
	echo "Usage: sh copy.sh"
	exit 1
}

# copy jar
echo "begin copy mai-auth "
cp ../mai-auth/target/mai-auth.jar ./mai/auth/jar

echo "begin copy mai-comment "
cp ../mai-comment/target/mai-comment.jar ./mai/comment/jar

echo "begin copy mai-file "
cp ../mai-file/target/mai-file.jar ./mai/file/jar

echo "begin copy mai-gateway "
cp ../mai-gateway/target/mai-gateway.jar ./mai/gateway/jar

echo "begin copy mai-message "
cp ../mai-message/target/mai-message.jar ./mai/message/jar

echo "begin copy mai-monitor "
cp ../mai-monitor/target/mai-monitor.jar  ./mai/monitor/jar

echo "begin copy mai-post "
cp ../mai-post/target/mai-post.jar ./mai/post/jar

echo "begin copy mai-search "
cp ../mai-search/target/mai-search.jar ./mai/search/jar

echo "begin copy mai-user "
cp ../mai-user/target/mai-user.jar ./mai/user/jar

