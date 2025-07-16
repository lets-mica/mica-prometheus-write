package net.dreamlu.mica.prometheus.write.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.prometheus.write.config.ConfigLoader;
import net.dreamlu.mica.prometheus.write.utils.PromPbUtils;
import net.dreamlu.mica.prometheus.write.utils.SnappyUtils;
import org.apache.kafka.clients.producer.Producer;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseStatus;
import org.tio.http.common.RequestLine;
import org.tio.http.common.handler.HttpRequestHandler;

import java.util.List;
import java.util.Map;

/**
 * prometheus write api
 *
 * @author L.cm
 */
@Slf4j
@RequiredArgsConstructor
public class PrometheusWriteHandler implements HttpRequestHandler {
	private final ConfigLoader config;
	private final Producer<String, byte[]> producer;

	@Override
	public HttpResponse handler(HttpRequest request) throws Exception {
		RequestLine requestLine = request.getRequestLine();
		String path = requestLine.getPath();
		if (!"/write".equals(path)) {
			return resp404(request);
		}
		// 解压缩 content-encoding -> snappy
		String contentEncoding = request.getHeader("content-encoding");
		byte[] requestBody = request.getBody();
		byte[] decompressed;
		if ("snappy".equals(contentEncoding)) {
			decompressed = SnappyUtils.decompress(requestBody);
		} else {
			throw new IllegalArgumentException("未知的 Content-Encoding 仅仅支持 snappy");
		}
		// 处理版本 x-prometheus-remote-write-version -> 0.1.0
		String prometheusRemoteWriteVersion = request.getHeader("x-prometheus-remote-write-version");
		// v 1.0 版协议
		List<Map<String, Object>> dataList;
		if ("0.1.0".equals(prometheusRemoteWriteVersion)) {
			dataList = PromPbUtils.decodeWriteRequestV1(decompressed);
		} else {
			dataList = PromPbUtils.decodeWriteRequestV2(decompressed);
		}
		log.info("dataList:{}", dataList);
		HttpResponse httpResponse = new HttpResponse(request);
		httpResponse.setStatus(HttpResponseStatus.C204);
		return httpResponse;
	}
}
