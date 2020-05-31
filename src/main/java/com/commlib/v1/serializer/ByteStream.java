package com.commlib.v1.serializer;

public class ByteStream {

    private final byte[] src;
    private final int offset;
    private final int length;
    private int mark;

    public ByteStream(byte[] src, int offset, int length) {
        this.src = src;
        this.offset = offset;
        this.length = length;
    }

    public void reset() {
        mark = offset;
    }

    public int nextInt() {
        int x = ((src[mark] & 0xFF) << 24) | ((src[1 + mark] & 0xFF) << 16) | ((src[2 + mark] & 0xFF) << 8) | ((src[3 + mark] & 0xFF));
        mark += 4;
        return x;
    }

    float fromByteArray(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }


}
