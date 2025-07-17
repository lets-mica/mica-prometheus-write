# Mica-Prometheus-Write

Mica-Prometheus-Write is a Java implementation project for handling Prometheus remote write requests (currently supports writing to Kafka). This project is primarily used to receive, decode, and process remote write data from Prometheus, which can be used to build storage backends or forwarding services for Prometheus data.

## ✨ Project Features

- Supports Prometheus remote write protocols v1 and v2.
- Provides Protobuf-based data structures for parsing and constructing Prometheus write requests.
- Offers HTTP interfaces to handle Prometheus remote write requests.
- Includes Snappy compression/decompression tools to support decompressing remote write data.
- Provides detailed MetricMetadata, TimeSeries, and Sample structures.
- Supports extensibility, allowing easy integration with other storage systems or data forwarding services.

## 🏗 Installation

The project is built using Maven and can be constructed directly via the following command:

```bash
mvn clean package
```

Then run `java -jar mica-prometheus-write-server.jar` directly. Configuration supports `config.properties` in the same directory as the jar or `config/config.properties`, and also supports environment variables.

Example `config.properties` configuration can be found in: [config.properties](mica-prometheus-write-server/src/test/resources/config.properties)

## 🔧 Configuration (prometheus.yml)

```yml
# Enable remote write
remote_write:
    - url: "http://127.0.0.1:8080/write"
#      protobuf_message: io.prometheus.write.v2.Request # Enable Prometheus v2 protobuf message format (v2 messages are more compact, default: v1)
#      basic_auth:  # Enable basic authentication
#          username: xxxxxx
#          password: xxxxxx
```

Note: For more configurations, please refer to: https://prometheus.io/docs/prometheus/latest/configuration/configuration/#remote_write

## 🍻 My Open Source Projects

- `mica-mqtt`: A simple and easy-to-use Java MQTT client and server: [https://gitee.com/dromara/mica-mqtt](https://gitee.com/dromara/mica-mqtt)
- `mica`: Core toolset for Spring Cloud microservices development: [https://gitee.com/596392912/mica](https://gitee.com/596392912/mica)
- `mica-auto`: Automatically generates Java SPI and Spring Boot configurations: [https://gitee.com/596392912/mica-auto](https://gitee.com/596392912/mica-auto)
