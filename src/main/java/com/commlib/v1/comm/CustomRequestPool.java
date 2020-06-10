package com.commlib.v1.comm;

import com.commlib.proto.Info;
import com.commlib.proto.RequestPool;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class CustomRequestPool implements RequestPool {

    private final Map<Info, Function<Info, Boolean>> requests;
    private final Collection<Listener> listeners;
    private final Thread thread;

    CustomRequestPool() {
        requests = new ConcurrentHashMap<>();
        listeners = new HashSet<>();
        thread = new Thread(this::analyze);
        thread.start();
    }

    private void analyze() {
        while (true) {

            long now = System.currentTimeMillis();
            Iterator<Map.Entry<Info, Function<Info, Boolean>>> it = requests.entrySet().iterator();
            it.forEachRemaining(entry -> {
                long duration = now - entry.getKey().timestamp();
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
    public void register(Listener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public <T extends Info> void insert(Info hash, Function<T, Boolean> reply) {
        if (reply != null)
            requests.put(hash, (Function<Info, Boolean>) reply);
    }

    @Override
    public void execute(Info response) {

        Function<Info, Boolean> reply = requests.remove(response);
        if (reply == null || !reply.apply(response)) {
            listeners
                    .parallelStream()
                    .forEach(x -> x.onResponse(response));

        }


    }

}
