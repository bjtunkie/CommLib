package com.commlib.v1.network;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelfProcessingDecodePacket extends BasePacket {

    private final ExecutorService service = Executors.newSingleThreadExecutor();
    private final ByteBuffer buffer = ByteBuffer.wrap(data);
    private volatile int mark;

    public void init(byte[] data, int dataLength) {
        buffer.put(data, 0, mark = dataLength);
    }


    @Override
    public final void decode() {
        service.submit(super::decode);
    }

    @Override
    public void reset() {
        super.reset();
    }
}
