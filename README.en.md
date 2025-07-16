# Mica-Prometheus-Write

Mica-Prometheus-Write is a Java implementation project for handling Prometheus remote write requests. This project is primarily used to receive Prometheus remote write data, decode and process it, and can be used to build storage backends or forwarding services for Prometheus data.

## Project Features

- Supports Prometheus remote write protocol versions v1 and v2.
- Provides Protobuf-based data structures for parsing and constructing Prometheus write requests.
- Offers HTTP interface handling for Prometheus remote write requests.
- Includes Snappy compression/decompression tools to support decompression of remote write data.
- Provides detailed MetricMetadata, TimeSeries, and Sample structures.
- Supports extensibility and can be easily integrated into other storage systems or data forwarding services.

## Installation

The project is built using Maven and can be built directly with the following command:

```bash
mvn clean package
```

## Usage

1. **Start the Service**  
   The project includes an `Application.java` class used to start the Prometheus write service.

2. **Handle Write Requests**  
   `PrometheusWriteHandler` implements the `HttpRequestHandler` interface to receive and process Prometheus remote write requests.

3. **Decompress Data**  
   The `SnappyUtils` class provides methods for decompressing Snappy-compressed data.

4. **Parse Request Body**  
   The `PromPbUtils` class provides methods to decode Prometheus write requests for both version v1 and v2.

## Directory Structure

- `mica-prometheus-write-core`: Core module containing Protobuf-compiled generated classes for handling Prometheus data models.
- `mica-prometheus-write-server`: Service module containing the HTTP service for receiving remote write requests.

## Dependencies

- Protobuf for handling data structures.
- Snappy for decompressing compressed data from remote write requests.

## config（prometheus.yml）

```yml
# enable prometheus remote write
remote_write:
  - url: "http://127.0.0.1:8080/write"
#    protobuf_message: io.prometheus.write.v2.Request # enable prometheus v2 protobuf message，default：v1
```

## License

This project uses the Apache-2.0 license. Please see the `LICENSE` file for details.
