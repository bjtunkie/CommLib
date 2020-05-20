package com.commlib.v1.network;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Header {

    /**
     * fixed size in bytes 24 bytes + data
     * / 16 (uniqueID) / 4 (topic) / 4 (length of data) / data
     */

    Function<Integer, byte[]> intToByteArray = PackUtils::toByteArray;
    BiFunction<byte[], Integer, Integer> fromByteArray = PackUtils::fromByteArray;

    void apply();

    void extract();

    default void apply(byte[] arr, String... info) {
        int mark = 16;
        System.arraycopy(info[0], 0, arr, 0, 16);
        for (int i = 1; i < info.length; i++) {
            String str = info[i];
            byte[] bytes = str.getBytes();
            byte[] length = intToByteArray.apply(bytes.length);

            System.arraycopy(length, 0, arr, mark, 4);
            System.arraycopy(bytes, 0, arr, mark + 4, bytes.length);

            mark = mark + bytes.length + 4;
        }
        for (String i : info) {
            byte[] bytes = i.getBytes();
            int length = bytes.length;
            System.arraycopy(bytes, 0, arr, mark, length);
            mark += length;
        }

    }

    default String[] extract(byte[] data) {
        String[] arr = new String[2];

        String uniqueID = arr[0] = new String(data, 0, 16);
        int topicLength = fromByteArray.apply(data, 16);
        String topicID = arr[1] = new String(data, 20, topicLength);

        return arr;
    }
}
