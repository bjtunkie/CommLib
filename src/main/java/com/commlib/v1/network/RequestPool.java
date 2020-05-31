package com.commlib.v1.network;

import com.commlib.v1.comm.BaseInfo;
import com.commlib.v1.comm.BaseInfoReply;

public interface RequestPool {
    RequestPool defaultRequestPool = new RequestPool() {
        @Override
        public void insert(BaseInfo info, BaseInfoReply reply) {

        }

        @Override
        public BaseInfoReply getAndRemove(BaseInfo info) {
            return null;
        }
    };
    int TIME_TO_LIVE = 5000; // In milliseconds

    void insert(BaseInfo info, BaseInfoReply reply);

    BaseInfoReply getAndRemove(BaseInfo info);

}
