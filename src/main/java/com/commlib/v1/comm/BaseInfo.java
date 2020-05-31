package com.commlib.v1.comm;

import com.commlib.v1.serializer.LWSerializable;

import java.io.Serializable;

public class BaseInfo implements Serializable, LWSerializable {

    private static final long serialVersionUID = -3667541950233299674L;
    private transient String toUniqueID;
    private transient int transmitCode;
    private transient long timestamp;

    private final String fromUniqueID;
    private final String requestID;
    private final String sessionID;

    public BaseInfo(int transmitCode, String fromID, String toID, String requestID, String sessionID) {
        this.transmitCode = transmitCode;
        this.fromUniqueID = fromID;
        this.toUniqueID = toID;
        this.requestID = requestID;
        this.sessionID = sessionID;
    }

    public void setTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public final String getFromUniqueID() {
        return fromUniqueID;
    }

    public final String getToUniqueID() {
        return toUniqueID;
    }

    public final String getRequestID() {
        return requestID;
    }

    public final String getSessionID() {
        return sessionID;
    }

    public int getTransmitCode() {
        return transmitCode;
    }

    @Override
    public boolean equals(Object o) {
        BaseInfo baseInfo = (BaseInfo) o;
        return requestID.equals(baseInfo.requestID);
    }

    @Override
    public int hashCode() {
        return requestID.hashCode();
    }

}
