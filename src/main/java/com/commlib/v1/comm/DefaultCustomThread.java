package com.commlib.v1.comm;

public class DefaultCustomThread extends CustomThread {
    public DefaultCustomThread(int hash, CustomThreadFactory factory) {
        super(hash, null, factory);
    }


    @Override
    protected void work(BaseInfo info, BaseInfoReply defaultListener) {

    }
}
