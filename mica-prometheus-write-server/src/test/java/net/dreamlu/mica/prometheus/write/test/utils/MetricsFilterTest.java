package net.dreamlu.mica.prometheus.write.test.utils;

import net.dreamlu.mica.prometheus.write.utils.MetricsFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * MetricsFilter 测试
 *
 * @author L.cm
 */
class MetricsFilterTest {

	@Test
	void test1() {
		MetricsFilter filter = MetricsFilter.from("node_*;kafka_*");
		Assertions.assertNotNull(filter);
		Assertions.assertTrue(filter.match("node_1"));
	}

}
