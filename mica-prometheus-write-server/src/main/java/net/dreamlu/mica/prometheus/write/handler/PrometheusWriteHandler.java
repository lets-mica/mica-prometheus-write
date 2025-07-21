package net.dreamlu.mica.prometheus.write.handler;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.prometheus.write.config.BasicAuthProperties;
import net.dreamlu.mica.prometheus.write.config.ConfigLoader;
import net.dreamlu.mica.prometheus.write.pojo.CountInfo;
import net.dreamlu.mica.prometheus.write.utils.MetricsFilter;
import net.dreamlu.mica.prometheus.write.utils.PromPbUtils;
import net.dreamlu.mica.prometheus.write.utils.SnappyUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.tio.http.common.*;
import org.tio.http.common.handler.HttpRequestHandler;
import org.tio.utils.hutool.StrUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * prometheus write api
 *
 * @author L.cm
 */
@Slf4j
public class PrometheusWriteHandler implements HttpRequestHandler {
	private static final String BASIC_AUTH_HEADER_NAME = "authorization";
	private static final String AUTHORIZATION_PREFIX = "Basic ";
	private final String basicAuthToken;
	private final MetricsFilter metricsFilter;
	private final String metricsSendTopic;
	private final Producer<String, Object> producer;

	public PrometheusWriteHandler(ConfigLoader config, Producer<String, Object> producer) {
		this.basicAuthToken = getBasicToken(config.getBasicAuthProperties());
		this.metricsFilter = config.getMetricsFilter();
		this.metricsSendTopic = config.getMetricsSendTopic();
		this.producer = producer;
	}

	@Override
	public HttpResponse handler(HttpRequest request) throws Exception {
		RequestLine requestLine = request.getRequestLine();
		String path = requestLine.getPath();
		// 暴露的端点，默认为 /write
		if (!"/write".equals(path) || !Method.POST.equals(requestLine.method)) {
			return resp404(request);
		}
		// 基础认证。是否认证成功
		if (!basicAuth(basicAuthToken, request)) {
			log.error("认证失败，请确认 prometheus remote_write 是否配置 basic_auth");
			return respStatus(request, HttpResponseStatus.C401);
		}
		// 解压缩 content-encoding -> snappy
		String contentEncoding = request.getHeader("content-encoding");
		byte[] requestBody = request.getBody();
		byte[] decompressed;
		if ("snappy".equals(contentEncoding)) {
			decompressed = SnappyUtils.decompress(requestBody);
		} else {
			throw new IllegalArgumentException("未知的 Content-Encoding，仅仅支持 snappy");
		}
		// 处理版本 x-prometheus-remote-write-version -> 0.1.0
		String prometheusRemoteWriteVersion = request.getHeader("x-prometheus-remote-write-version");
		HttpResponse httpResponse = new HttpResponse(request);
		// 处理不同版本的数据，0.1.0 为 v1 版协议
		if ("0.1.0".equals(prometheusRemoteWriteVersion)) {
			// 解码、过滤并发送数据
			PromPbUtils.decodeWriteRequestV1(decompressed, metricsFilter, this::sendToKafka);
		} else {
			// v2 版本需要返回数量
			CountInfo countInfo = PromPbUtils.decodeWriteRequestV2(decompressed, metricsFilter, this::sendToKafka);
			httpResponse.addHeader("X-Prometheus-Remote-Write-Samples-Written", countInfo.getSamplesCount());
			httpResponse.addHeader("X-Prometheus-Remote-Write-Histograms-Written", countInfo.getHistogramsCount());
			httpResponse.addHeader("X-Prometheus-Remote-Write-Exemplars-Written", countInfo.getExemplarsCount());
		}
		// 正常返回 204
		httpResponse.setStatus(HttpResponseStatus.C204);
		return httpResponse;
	}

	/**
	 * 发送到 kafka
	 *
	 * @param metricsName 指标名称
	 * @param dataMap     dataMap
	 */
	private void sendToKafka(String metricsName, Map<String, Object> dataMap) {
		// 组装 kafka 数据
		ProducerRecord<String, Object> record = new ProducerRecord<>(
			metricsSendTopic, metricsName, JSON.toJSONBytes(dataMap)
		);
		// 发送 kafka
		log.info("metrics:{} 发送到 kafka", metricsName);
		producer.send(record);
	}

	/**
	 * 获取基础认证的 token
	 *
	 * @param basicAuthProperties BasicAuthProperties
	 * @return token
	 */
	private static String getBasicToken(BasicAuthProperties basicAuthProperties) {
		// 1. 没有开启认证
		if (!basicAuthProperties.isEnable()) {
			return null;
		}
		// 2. 账号
		String username = basicAuthProperties.getUsername();
		if (StrUtil.isBlank(username)) {
			throw new IllegalArgumentException("basic_auth 认证已开启，但是 auth.basic_auth.username 配置为空");
		}
		// 3. 密码
		String password = basicAuthProperties.getPassword();
		if (StrUtil.isBlank(password)) {
			throw new IllegalArgumentException("basic_auth 认证已开启，但是 auth.basic_auth.password 配置为空");
		}
		// 4. 生成 token
		byte[] tokenBytes = (username + ':' + password).getBytes(StandardCharsets.UTF_8);
		return Base64.getEncoder().encodeToString(tokenBytes);
	}

	/**
	 * 基础认证
	 *
	 * @param basicAuthToken BasicAuthProperties
	 * @return 是否认证
	 */
	private static boolean basicAuth(String basicAuthToken, HttpRequest request) {
		// 1. 为 null 是不需要认证的情况
		if (basicAuthToken == null) {
			return true;
		}
		// 2. 获取认证头
		String authorization = request.getHeader(BASIC_AUTH_HEADER_NAME);
		if (StrUtil.isBlank(authorization)) {
			return false;
		}
		// 3. 校验 token
		int length = AUTHORIZATION_PREFIX.length();
		if (length >= authorization.length()) {
			return false;
		}
		return basicAuthToken.equals(authorization.substring(length));
	}
}
