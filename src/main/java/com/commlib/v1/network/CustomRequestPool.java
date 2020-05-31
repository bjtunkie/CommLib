package com.commlib.v1.network;

import com.commlib.v1.comm.BaseInfo;
import com.commlib.v1.comm.BaseInfoReply;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomRequestPool implements RequestPool {

    private final Map<BaseInfo, BaseInfoReply> requests = new ConcurrentHashMap<>();
    private final Thread thread = new Thread(this::analyze);

    public CustomRequestPool() {

        thread.start();
    }

    private void analyze() {
        while (true) {

            long now = System.currentTimeMillis();
            Iterator<Map.Entry<BaseInfo, BaseInfoReply>> it = requests.entrySet().iterator();
            it.forEachRemaining(entry -> {
                long duration = now - entry.getKey().getTimestamp();
                if (duration > TIME_TO_LIVE) {
                    it.remove();
                }
            });

            try {
                Thread.sleep(TIME_TO_LIVE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void insert(BaseInfo info, BaseInfoReply reply) {
        requests.put(info, reply);
    }

    @Override
    public BaseInfoReply getAndRemove(BaseInfo info) {
        return requests.remove(info);
    }
}
