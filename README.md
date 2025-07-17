# Mica-Prometheus-Write

Mica-Prometheus-Write 是一个用于处理 Prometheus 远程写入请求的 Java 实现项目（目前支持写入 kafka）。该项目主要用于接收 Prometheus 的远程写入数据，解码并处理这些数据，可以用于构建 Prometheus 数据的存储后端或转发服务。

## 项目特点

- 支持 Prometheus 远程写入 v1 和 v2 版本协议。
- 提供基于 Protobuf 的数据结构，用于解析和构建 Prometheus 的写入请求。
- 提供 HTTP 接口处理 Prometheus 的远程写入请求。
- 包含 Snappy 压缩解压工具，支持解压远程写入数据。
- 提供详细的 MetricMetadata、TimeSeries、Sample
- 支持扩展，可轻松集成到其他存储系统或数据转发服务中。

## 安装

项目使用 Maven 构建，可以直接通过以下命令进行构建：

```bash
mvn clean package
```

然后直接运行 `java -jar mica-prometheus-write-server.jar` 即可，配置支持 jar 统计目录 `config.properties` 或 `config/config.properties`，也支持环境变量。

`config.properties` 配置示例详见：[config.properties](mica-prometheus-write-server/src/test/resources/config.properties)

## 配置（prometheus.yml）

```yml
# 开启远程写出
remote_write:
    - url: "http://127.0.0.1:8080/write"
#      protobuf_message: io.prometheus.write.v2.Request # 开启 prometheus v2 版 protobuf 消息格式，v2 版消息更加紧凑，默认：v1
#      basic_auth:  # 开启基础认证
#          username: xxxxxx
#          password: xxxxxx
```

注意：更多配置请查看：https://prometheus.io/docs/prometheus/latest/configuration/configuration/#remote_write

## 许可证

本项目使用 Apache-2.0 协议。详见 `LICENSE` 文件。
