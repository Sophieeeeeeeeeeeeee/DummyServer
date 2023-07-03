# DummyServer
DummyServer

# 步骤
mvn package将应用打成mock-1.0.0-jar-with-dependencies.jar。
mock-1.0.0-jar-with-dependencies.jar和config.properties，response.txt放相同目录下。
在该目录下执行jarjava -jar mock-1.0.0-jar-with-dependencies.jar

# Request
curl -v -k -X POST --data-urlencode "{"key1": 200,"key2": "value2","key3": {"nestedKey1": "nestedValue1","nestedKey2": "nestedValue2"}}" http://127.0.0.1:8080 注：切换host
