## protobuf 编译

`proto` 文件来源 `prometheus` 项目：https://github.com/prometheus/prometheus/tree/main/prompb

目前有2个版本，v1 和 v2，可通过请求头 ContentType 判断版本。

下载 windows 对应的 `protocolBuffers` 编译工具：https://github.com/protocolbuffers/protobuf/releases

**编译**：cd 到对应版本的目录，然后执行：
```bash
protoc.exe -I=. --java_out=../mica-prometheus-write-core/src/main/java *.proto
```
