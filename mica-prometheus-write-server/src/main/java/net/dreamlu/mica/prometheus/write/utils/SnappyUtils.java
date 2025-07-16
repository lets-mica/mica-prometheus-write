package net.dreamlu.mica.prometheus.write.utils;

import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * Snappy 解压
 *
 * @author L.cm
 */
public class SnappyUtils {

    /**
     * 使用 Snappy 解压字节数组
     * @param compressed 压缩后的字节数组
     * @return 解压后的原始字节数组
     */
    public static byte[] decompress(byte[] compressed) throws IOException {
        if (compressed == null || compressed.length == 0) {
            return new byte[0];
        }
        return Snappy.uncompress(compressed);
    }

}
