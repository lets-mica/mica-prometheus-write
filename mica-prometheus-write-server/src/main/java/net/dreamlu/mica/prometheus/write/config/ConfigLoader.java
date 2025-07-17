package net.dreamlu.mica.prometheus.write.config;

import net.dreamlu.mica.prometheus.write.utils.MetricsFilter;
import org.tio.utils.hutool.ResourceUtil;
import org.tio.utils.hutool.StrUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置加载
 *
 * @author L.cm
 */
public class ConfigLoader {
	private final Properties properties = new Properties();

	/**
	 * 配置加载优先级顺序
	 */
	private static final String[] CONFIG_LOCATIONS = {
		"config/config.properties",           // 优先加载项目根目录下 config 目录
		"config.properties",                  // 次优先加载项目根目录下
		"classpath:config/config.properties", // 然后加载 classpath 下 config 目录
		"classpath:config.properties"         // 最后加载 classpath 根目录
	};

	public ConfigLoader() {
		loadPropertiesFromFiles();
		loadPropertiesFromEnv();
	}

	private void loadPropertiesFromFiles() {
		for (String location : CONFIG_LOCATIONS) {
			try {
				if (StrUtil.startWithIgnoreCase(location, "classpath:")) {
					try (InputStream inputStream = ResourceUtil.getResourceAsStream(location)) {
						if (inputStream != null) {
							properties.load(inputStream);
						}
					}
				} else {
					try (InputStream inputStream = ResourceUtil.getFileResource(location)) {
						properties.load(inputStream);
					}
				}
			} catch (IOException | IllegalArgumentException ignore) {
				// 忽略加载失败的文件，继续尝试下一个
			}
		}
	}

	private void loadPropertiesFromEnv() {
		// 系统环境变量优先级最高
		System.getenv().forEach((key, value) -> {
			// 将环境变量名转换为properties风格的key（如DATABASE_URL -> database.url）
			String propKey = key.toLowerCase().replace("_", ".");
			properties.put(propKey, value);
		});
		// 系统属性次之
		properties.putAll(System.getProperties());
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public Properties getAllProperties() {
		return new Properties(properties);
	}

	public int getServerPort() {
		return Integer.parseInt(getProperty("server.port", "8080"));
	}

	public String getSendTopic() {
		return getProperty("send.topic", "metrics");
	}

	/**
	 * 获取 Kafka 相关配置
	 *
	 * @return 去除 "kafka." 前缀的 Kafka 配置 Properties
	 */
	public Properties getKafkaProperties() {
		Properties kafkaProps = new Properties();
		// 筛选所有以 "kafka."开头的配置项
		for (String propertyName : properties.stringPropertyNames()) {
			if (propertyName.startsWith("kafka.")) {
				// 去掉 "kafka." 前缀，保留剩余部分作为新的key
				String newName = propertyName.substring(6);
				kafkaProps.setProperty(newName, properties.getProperty(propertyName));
			}
		}
		return kafkaProps;
	}

	/**
	 * 获取认证配置
	 *
	 * @return BasicAuthProperties
	 */
	public BasicAuthProperties getBasicAuthProperties() {
		String basicAuthEnable = this.getProperty("auth.basic_auth.enable", "false").toLowerCase();
		String basicAuthUsername = this.getProperty("auth.basic_auth.username", null);
		String basicAuthPassword = this.getProperty("auth.basic_auth.password", null);
		return new BasicAuthProperties("true".equals(basicAuthEnable), basicAuthUsername, basicAuthPassword);
	}

	/**
	 * 指标过滤器
	 *
	 * @return MetricFilter
	 */
	public MetricsFilter getMetricsFilter() {
		return MetricsFilter.from(this.getProperty("metrics.filter"));
	}

}
