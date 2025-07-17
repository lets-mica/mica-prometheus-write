package net.dreamlu.mica.prometheus.write.utils;

import org.tio.utils.hutool.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * 指标过滤
 *
 * @author L.cm
 */
public class MetricsFilter {
	/**
	 * 规则分割符，支持 , 或 ;
	 */
	private static final Pattern PATTERN = Pattern.compile("[,;]");
	private final List<Predicate<String>> filterPredicate = new ArrayList<>();

	/**
	 * 构造 MetricsFilter，可能为 null
	 *
	 * @param patterns patterns
	 * @return MetricsFilter
	 */
	public static MetricsFilter from(String patterns) {
		// 如果规则为空，直接返回 null
		if (StrUtil.isBlank(patterns)) {
			return null;
		}
		MetricsFilter filter = new MetricsFilter();
		String[] patternArray = PATTERN.split(patterns);
		for (String pattern : patternArray) {
			filter.addPattern(pattern);
		}
		return filter;
	}

	private void addFilter(Predicate<String> filter) {
		filterPredicate.add(filter);
	}

	/**
	 * 添加规则
	 *
	 * @param pattern pattern
	 */
	public void addPattern(String pattern) {
		if (StrUtil.isBlank(pattern)) {
			throw new IllegalArgumentException("指标过滤规则 pattern 不能为空");
		}
		// 判断是否为前缀匹配（不包含特殊正则字符）
		if (isSimplePrefixPattern(pattern)) {
			this.addFilter(s -> s.startsWith(pattern.substring(0, pattern.length() - 1)));
		} else {
			this.addFilter(s -> Pattern.compile(pattern).matcher(s).matches());
		}
	}

	/**
	 * 是否匹配
	 *
	 * @param metricName metricName
	 * @return 是否匹配
	 */
	public boolean match(String metricName) {
		// 如果没有过滤器
		if (filterPredicate.isEmpty()) {
			return true;
		}
		// 判断是否有任意一个匹配
		return filterPredicate.stream().anyMatch(filter -> filter.test(metricName));
	}

	/**
	 * 判断是否简单的前缀判断
	 *
	 * @param pattern pattern
	 * @return 是否简单规则
	 */
	private static boolean isSimplePrefixPattern(String pattern) {
		// 如果pattern以*结尾，则认为是前缀匹配（去掉*）
		if (pattern.endsWith("*")) {
			pattern = pattern.substring(0, pattern.length() - 1);
		}
		// 检查是否包含正则特殊字符
		for (char c : pattern.toCharArray()) {
			if (".*+?^$[](){}|\\".indexOf(c) != -1) {
				return false;
			}
		}
		return true;
	}

}
