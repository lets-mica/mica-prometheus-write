package net.dreamlu.mica.prometheus.write.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 数量信息
 *
 * @author L.cm
 */
@Getter
@RequiredArgsConstructor
public class CountInfo {
    private final long samplesCount;
    private final long histogramsCount;
    private final long exemplarsCount;
}
