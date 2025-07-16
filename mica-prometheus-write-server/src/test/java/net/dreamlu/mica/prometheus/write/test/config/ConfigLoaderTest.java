package net.dreamlu.mica.prometheus.write.test.config;

import net.dreamlu.mica.prometheus.write.config.ConfigLoader;

import java.util.Properties;

/**
 * 配置加载测试
 *
 * @author L.cm
 */
public class ConfigLoaderTest {

	// 测试用例
	public static void main(String[] args) {
		// 模拟环境变量和系统属性
		System.setProperty("kafka.bootstrap.servers", "localhost:9092");
		System.setProperty("kafka.client.id", "test-client");

		ConfigLoader config = new ConfigLoader();

		// 打印所有加载的配置
		System.out.println("Loaded properties:");
		config.getAllProperties().forEach((key, value) -> {
			System.out.println(key + "=" + value);
		});

		// 测试获取特定配置
		String dbUrl = config.getProperty("database.url");
		System.out.println("\nDatabase URL: " + dbUrl);

		Properties kafkaProperties = config.getKafkaProperties();
		System.out.println(kafkaProperties);
	}

}
