package net.dreamlu.mica.prometheus.write.utils;

import io.prometheus.prompb.RemoteProto;
import io.prometheus.prompb.TypesProto;
import io.prometheus.write.v2.Types;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * prometheus 数据包解析
 *
 * @author L.cm
 */
public class PromPbUtils {

	/**
	 * 解码
	 *
	 * @param data data
	 * @return 集合
	 * @throws IOException IOException
	 */
	public static List<Map<String, Object>> decodeWriteRequestV1(byte[] data) throws IOException {
		RemoteProto.WriteRequest writeRequest = RemoteProto.WriteRequest.parseFrom(data);
		List<TypesProto.TimeSeries> timeseriesList = writeRequest.getTimeseriesList();
		List<Map<String, Object>> dataList = new ArrayList<>();
		for (TypesProto.TimeSeries timeSeries : timeseriesList) {
			List<TypesProto.Label> labelsList = timeSeries.getLabelsList();
			Map<String, Object> labelsMap = new HashMap<>();
			for (TypesProto.Label label : labelsList) {
				String labelName = label.getName();
				String labelValue = label.getValue();
				labelsMap.put(labelName, labelValue);
			}
			List<TypesProto.Sample> samplesList = timeSeries.getSamplesList();

			for (TypesProto.Sample sample : samplesList) {
				long timestamp = sample.getTimestamp();
				Object name = labelsMap.get("__name__");

				Map<String, Object> dataMap = new HashMap<>();
				dataMap.put("timestamp", timestamp);
				dataMap.put("value", sample.getValue());
				dataMap.put("name", name);
				dataMap.put("labels", labelsMap);

				dataList.add(dataMap);
			}
		}
		return dataList;
	}

	/**
	 * 解码
	 *
	 * @param data data
	 * @return 集合
	 * @throws IOException IOException
	 */
	public static List<Map<String, Object>> decodeWriteRequestV2(byte[] data) throws IOException {
		Types.Request writeRequest = Types.Request.parseFrom(data);
		List<Types.TimeSeries> timeseriesList = writeRequest.getTimeseriesList();
		List<Map<String, Object>> dataList = new ArrayList<>();
		for (Types.TimeSeries timeSeries : timeseriesList) {
			int labelsRefsCount = timeSeries.getLabelsRefsCount();
			// 每 2 个索引对应一个标签 (name_idx, value_idx)
			Map<String, Object> labelsMap = new HashMap<>();
			for (int i = 0; i < labelsRefsCount; i += 2) {
				int nameIdx = timeSeries.getLabelsRefs(i);
				int valueIdx = timeSeries.getLabelsRefs(i + 1);
				String name = writeRequest.getSymbols(nameIdx);
				String value = writeRequest.getSymbols(valueIdx);
				labelsMap.put(name, value);
			}

			List<Types.Sample> samplesList = timeSeries.getSamplesList();
			for (Types.Sample sample : samplesList) {
				long timestamp = sample.getTimestamp();
				Object name = labelsMap.get("__name__");

				Map<String, Object> dataMap = new HashMap<>();
				dataMap.put("timestamp", timestamp);
				dataMap.put("value", sample.getValue());
				dataMap.put("name", name);
				dataMap.put("labels", labelsMap);

				dataList.add(dataMap);
			}
		}
		return dataList;
	}

}
