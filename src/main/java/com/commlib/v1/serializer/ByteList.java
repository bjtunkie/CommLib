package com.commlib.v1.serializer;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class ByteList extends LinkedList<ByteBuffer> {

    public ByteList(byte[] data, int offset, int countOfObjects) {
        parse(data, offset, countOfObjects);
    }

    private void parse(byte[] data, int offset, int countOfObjects) {
        for (int i = 0, x = offset; i < countOfObjects; i++) {
            int len = ((data[x] & 0xFF) << 24) |
                    ((data[x + 1] & 0xFF) << 16) |
                    ((data[x + 2] & 0xFF) << 8) |
                    ((data[x + 3] & 0xFF));
            ByteBuffer buffer = ByteBuffer.wrap(data, x + 4, len);
            this.push(buffer);
            x += 4 + len;
        }
    }

/*    private void parse(byte[] data, int offset, int length) {
        int mark = offset;
        int i = 0;
        while (mark + 5 < length) {
            int x = mark;
            int len = ((data[x] & 0xFF) << 24) |
                    ((data[x + 1] & 0xFF) << 16) |
                    ((data[x + 2] & 0xFF) << 8) |
                    ((data[x + 3] & 0xFF));
            ByteBuffer buffer = ByteBuffer.wrap(data, x + 4, len);
            this.push(buffer);
            mark = x + 4 + len;
        }
    }*/


}
