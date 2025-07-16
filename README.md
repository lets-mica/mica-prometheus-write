# Mica-Prometheus-Write

Mica-Prometheus-Write 是一个用于处理 Prometheus 远程写入请求的 Java 实现项目。该项目主要用于接收 Prometheus 的远程写入数据，解码并处理这些数据，可以用于构建 Prometheus 数据的存储后端或转发服务。

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
mvn clean install
```

## 使用

1. **启动服务**  
   项目包含一个 `Application.java` 类，用于启动 Prometheus 写入服务。

2. **处理写入请求**  
   `PrometheusWriteHandler` 实现了 `HttpRequestHandler` 接口，用于接收并处理 Prometheus 的远程写入请求。

3. **解压数据**  
   `SnappyUtils` 类提供了 Snappy 厶缩数据的解压方法。

4. **解析请求体**  
   `PromPbUtils` 类提供了解码 Prometheus v1 和 v2 版本写入请求的方法。

## 示例

以下是如何使用 `PromPbUtils` 解码远程写入请求的示例：

```java
byte[] data = // Prometheus 写入请求的 byte 数据
List<Map<String, Object>> decoded = PromPbUtils.decodeWriteRequestV2(data);
```

## 目录结构

- `mica-prometheus-write-core`: 核心模块，包含 Protobuf 编译生成的类，用于处理 Prometheus 的数据模型。
- `mica-prometheus-write-server`: 服务模块，包含 HTTP 服务，用于接收远程写入请求。

## 依赖

- Protobuf 用于处理数据结构。
- Snappy 用于解压远程写入请求的压缩数据。

## 配置（prometheus.yml）

```yml
# 开启远程写出
remote_write:
  - url: "http://127.0.0.1:8080/write"
#    protobuf_message: io.prometheus.write.v2.Request # 开启 prometheus v2 版 protobuf 消息格式，v2 版消息更加紧凑，默认：v1
```

## 许可证

本项目使用 Apache-2.0 协议。详见 `LICENSE` 文件。

## 贡献

欢迎贡献代码！如需贡献，请提交 Pull Request 到本项目的 Gitee 仓库。

## 联系

如有任何问题，欢迎在 Gitee 上提交 Issue 或联系项目维护者。