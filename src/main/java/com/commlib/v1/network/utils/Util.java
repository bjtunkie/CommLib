package com.commlib.v1.network.utils;

public class Util {
    private static final Util instance = new Util();

    public static byte[] intToByteArray(int value) {
        return instance.toByteArray(value);
    }

    public static int byteArrayToInt(byte[] bytes, int offset) {
        return instance.fromByteArray(bytes, offset);
    }

    public int getNumberOfSubPackets(int lengthOfBuffer, int lengthOfHeader, long lengthOfData) {
        return (int) ((lengthOfData) / (lengthOfBuffer - lengthOfHeader));
    }


    public byte[] toByteArray(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value};
    }

    public void fromIntToByteArray(int value, byte[] src, int offset) {
        src[offset] = (byte) (value >> 24);
        src[1 + offset] = (byte) (value >> 16);
        src[2 + offset] = (byte) (value >> 8);
        src[3 + offset] = (byte) (value);
    }


    // packing an array of 4 bytes to an int, big endian, clean code
    public int fromByteArray(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                ((bytes[3] & 0xFF));
    }

    public int fromByteArray(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) |
                ((bytes[1 + offset] & 0xFF) << 16) |
                ((bytes[2 + offset] & 0xFF) << 8) |
                ((bytes[3 + offset] & 0xFF));
    }

}

