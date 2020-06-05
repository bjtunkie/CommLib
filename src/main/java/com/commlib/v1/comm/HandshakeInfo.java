package com.commlib.v1.comm;

import com.commlib.v1.network.TransmitCode;

import java.io.Serializable;

public final class HandshakeInfo implements Serializable {
    private String uniqueID;
    private transient int transmitCode;
    private transient long timestamp;

    public HandshakeInfo(String uniqueID) {
        this.uniqueID = uniqueID;
        this.transmitCode = TransmitCode.HANDSHAKE_CONNECTION_WITH_SERVER;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public int getTransmitCode() {
        return transmitCode;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
