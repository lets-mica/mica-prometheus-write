package net.dreamlu.mica.prometheus.write.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * basic_auth 认证
 *
 * @author L.cm
 */
@Data
@RequiredArgsConstructor
public class BasicAuthProperties {

	/**
	 * 是否启用
	 */
	private final boolean enable;
	/**
	 * 用户名
	 */
	private final String username;
	/**
	 * 密码
	 */
	private final String password;

}
