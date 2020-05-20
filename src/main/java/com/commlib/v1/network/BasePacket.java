package com.commlib.v1.network;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class BasePacket implements Reusable {
    /**
     * fixed and variable sizes
     * / 16 (uniqueID) / 4 (=sizeOfN) / n
     */

    static final Function<Integer, byte[]> intToByteArray = PackUtils::toByteArray;
    static final BiFunction<byte[], Integer, Integer> fromByteArray = PackUtils::fromByteArray;
    protected final byte[] data = new byte[1024 * 1024 * 1]; // 1 MB
    String uniqueID;
    String topic;

    protected final void apply(String uniqueID, String topic) {
        /**
         * apply onto the data array
         */
        byte[] src;
        byte[] dst = data;
        byte[] lengthOfTopic;

        System.arraycopy(uniqueID.getBytes(), 0, dst, 0, 16);

        src = topic.getBytes();
        lengthOfTopic = intToByteArray.apply(src.length);
        System.arraycopy(lengthOfTopic, 0, dst, 16, 4);
        System.arraycopy(src, 0, dst, 20, src.length);

        this.uniqueID = uniqueID;
        this.topic = topic;

    }

    protected final void extract() {

        int topicLength = fromByteArray.apply(data, 16);
        uniqueID = new String(data, 0, 16);
        topic = new String(data, 20, topicLength);

    }

    @Override
    public void reset() {

    }
}
