package net.dreamlu.mica.prometheus.write.utils;

import io.prometheus.prompb.RemoteProto;
import io.prometheus.prompb.TypesProto;
import io.prometheus.write.v2.Types;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.prometheus.write.pojo.CountInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * prometheus 数据包解析
 *
 * @author L.cm
 */
@Slf4j
@UtilityClass
public class PromPbUtils {

    /**
     * 解码
     *
     * @param data data
     * @throws IOException IOException
     */
    public static void decodeWriteRequestV1(
        byte[] data, MetricsFilter metricsFilter, BiConsumer<String, Map<String, Object>> consumer
    ) throws IOException {
        RemoteProto.WriteRequest writeRequest = RemoteProto.WriteRequest.parseFrom(data);
        List<TypesProto.TimeSeries> timeseriesList = writeRequest.getTimeseriesList();
        for (TypesProto.TimeSeries timeSeries : timeseriesList) {
            List<TypesProto.Label> labelsList = timeSeries.getLabelsList();
            Map<String, String> labelsMap = new HashMap<>();
            for (TypesProto.Label label : labelsList) {
                String labelName = label.getName();
                String labelValue = label.getValue();
                labelsMap.put(labelName, labelValue);
            }
            // 指标名过滤
            String name = labelsMap.get("__name__");
            if (metricsFilter != null && !metricsFilter.match(name)) {
                log.debug("metrics:{} 不符合 metrics.filter 配置规则，已过滤", name);
                continue;
            }
            // 数据处理
            List<TypesProto.Sample> samplesList = timeSeries.getSamplesList();
            for (TypesProto.Sample sample : samplesList) {
                long timestamp = sample.getTimestamp();
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("timestamp", timestamp);
                dataMap.put("value", sample.getValue());
                dataMap.put("name", name);
                dataMap.put("labels", labelsMap);
                // 处理数据
                consumer.accept(name, dataMap);
            }
        }
    }

    /**
     * 解码
     *
     * @param data data
     * @return 集合
     * @throws IOException IOException
     */
    public static CountInfo decodeWriteRequestV2(
        byte[] data, MetricsFilter metricsFilter, BiConsumer<String, Map<String, Object>> consumer
    ) throws IOException {
        Types.Request writeRequest = Types.Request.parseFrom(data);
        List<Types.TimeSeries> timeseriesList = writeRequest.getTimeseriesList();
        long samplesCount = 0;
        long histogramsCount = 0;
        long exemplarsCount = 0;
        for (Types.TimeSeries timeSeries : timeseriesList) {
            int labelsRefsCount = timeSeries.getLabelsRefsCount();
            // 每 2 个索引对应一个标签 (name_idx, value_idx)
            Map<String, String> labelsMap = new HashMap<>();
            for (int i = 0; i < labelsRefsCount; i += 2) {
                int nameIdx = timeSeries.getLabelsRefs(i);
                int valueIdx = timeSeries.getLabelsRefs(i + 1);
                String name = writeRequest.getSymbols(nameIdx);
                String value = writeRequest.getSymbols(valueIdx);
                labelsMap.put(name, value);
            }
            // 指标数量
            samplesCount += timeSeries.getSamplesCount();
            histogramsCount += timeSeries.getHistogramsCount();
            exemplarsCount += timeSeries.getExemplarsCount();
            // 指标名过滤
            String name = labelsMap.get("__name__");
            if (metricsFilter != null && !metricsFilter.match(name)) {
                log.debug("metrics:{} 不符合 metrics.filter 配置规则，已过滤", name);
                continue;
            }
            // 数据处理
            List<Types.Sample> samplesList = timeSeries.getSamplesList();
            for (Types.Sample sample : samplesList) {
                long timestamp = sample.getTimestamp();
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("timestamp", timestamp);
                dataMap.put("value", sample.getValue());
                dataMap.put("name", name);
                dataMap.put("labels", labelsMap);
                // 处理数据
                consumer.accept(name, dataMap);
            }
        }
        return new CountInfo(samplesCount, histogramsCount, exemplarsCount);
    }

}
