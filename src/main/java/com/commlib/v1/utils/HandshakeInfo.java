package com.commlib.v1.utils;

import com.commlib.proto.AbstractInfo;
import com.commlib.v1.comm.Constant;

public final class HandshakeInfo extends AbstractInfo {
    public static final int CODE = 1;

    public HandshakeInfo(String srcUniqueID, String dstUniqueID, String requestID) {
        super(CODE, srcUniqueID, dstUniqueID, dstUniqueID, requestID, Constant.DEFAULT_ID);
    }

}
