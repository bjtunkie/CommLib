package com.commlib.proto;

public abstract class AbstractInfo implements Info {
    private long timestamp;

    private final int transmitCode;
    private final String requestID;
    private final String srcUniqueID;
    private final String dstUniqueID;
    private final String gatewayUniqueID;
    private final String sessionID;

    private String responseID; // basically requestID if you are responding to any

    public AbstractInfo(int transmitCode, String srcUniqueID, String dstUniqueID, String gatewayUniqueID, String requestID, String sessionID) {
        this.transmitCode = transmitCode;
        this.requestID = requestID;
        this.srcUniqueID = srcUniqueID;
        this.dstUniqueID = dstUniqueID;
        this.gatewayUniqueID = gatewayUniqueID;
        this.sessionID = sessionID;

    }

    public String getSessionID() {
        return sessionID;
    }

    public String getResponseID() {
        return responseID;
    }

    public String getGatewayUniqueID() {
        return gatewayUniqueID;
    }

    public String getSrcUniqueID() {
        return srcUniqueID;
    }

    public String getDstUniqueID() {
        return dstUniqueID;
    }

    public void setTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public final String uniqueID() {
        return gatewayUniqueID;
    }

    @Override
    public final String requestID() {
        return requestID;
    }

    @Override
    public final int transmitCode() {
        return transmitCode;
    }

    @Override
    public final long timestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractInfo that = (AbstractInfo) o;
        return requestID.equals(that.requestID);
    }

    @Override
    public final int hashCode() {
        return requestID.hashCode();
    }
}
