package com.commlib.v1.network;

public class PackUtils {

    public static int getNumberOfSubPackets(int lengthOfBuffer, int lengthOfHeader, long lengthOfData) {
        return (int) ((lengthOfData) / (lengthOfBuffer - lengthOfHeader));
    }


    public static byte[] toByteArray(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value};
    }

    public static void fromIntToByteArray(int value, byte[] src, int offset) {
        src[offset] = (byte) (value >> 24);
        src[1 + offset] = (byte) (value >> 16);
        src[2 + offset] = (byte) (value >> 8);
        src[3 + offset] = (byte) (value);
    }


    // packing an array of 4 bytes to an int, big endian, clean code
    public static int fromByteArray(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                ((bytes[3] & 0xFF));
    }

    public static int fromByteArray(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) |
                ((bytes[1 + offset] & 0xFF) << 16) |
                ((bytes[2 + offset] & 0xFF) << 8) |
                ((bytes[3 + offset] & 0xFF));
    }

}

