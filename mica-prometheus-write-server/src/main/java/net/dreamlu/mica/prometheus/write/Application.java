package net.dreamlu.mica.prometheus.write;

import net.dreamlu.mica.prometheus.write.config.ConfigLoader;
import net.dreamlu.mica.prometheus.write.handler.PrometheusWriteHandler;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.tio.http.server.HttpServerStarter;

import java.io.IOException;
import java.util.Properties;

/**
 * 应用
 *
 * @author L.cm
 */
public class Application {

	public static void main(String[] args) throws IOException {
		// 1. 加载配置
		ConfigLoader config = new ConfigLoader();
		// 2. 配置 kafka 生产者实例
		Properties kafkaProperties = config.getKafkaProperties();
		Producer<String, byte[]> producer = new KafkaProducer<>(kafkaProperties);
		// 3. 启动服务
		PrometheusWriteHandler handler = new PrometheusWriteHandler(config, producer);
		HttpServerStarter httpServerStarter = new HttpServerStarter(config.getServerPort(), handler);
		httpServerStarter.start();
	}

}
