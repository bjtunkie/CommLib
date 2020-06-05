package com.commlib.specs;

import com.commlib.v1.network.TCPConnection;

public interface Util {

    /****
     public int getNumberOfSubPackets(int lengthOfBuffer, int lengthOfHeader, long lengthOfData) {
     return (int) ((lengthOfData) / (lengthOfBuffer - lengthOfHeader));
     }
     ***/

    /****
    public TCPConnection findConnectionBasedOnIP(String ip) {
        // Parse IP parts into an int array
        byte[] addr = new byte[4];
        String[] parts = ip.split("\\.");

        for (int i = 0; i < 4; i++) {
            addr[i] = (byte) Integer.parseInt(parts[i]);
        }
        int ipAddr = ((addr[0] & 0xFF) << 24) | ((addr[1] & 0xFF) << 16) | ((addr[2] & 0xFF) << 8) | ((addr[3] & 0xFF));
        return findConnectionBasedOnIP(ipAddr);
    }

    ****/

    default byte[] intToByteArray(int value) {
        return new byte[]{(byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value};
    }

    default void intToByteArray(int value, byte[] src, int offset) {
        src[offset] = (byte) (value >> 24);
        src[1 + offset] = (byte) (value >> 16);
        src[2 + offset] = (byte) (value >> 8);
        src[3 + offset] = (byte) (value);
    }

    default int byteArrayToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | ((bytes[3] & 0xFF));
    }

    default int byteArrayToInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) | ((bytes[1 + offset] & 0xFF) << 16) | ((bytes[2 + offset] & 0xFF) << 8) | ((bytes[3 + offset] & 0xFF));
    }

    default void putInt(int value, byte[] dst, int offset) {
        intToByteArray(value, dst, offset);
    }
}

