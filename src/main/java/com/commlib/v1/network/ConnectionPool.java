package com.commlib.v1.network;

import java.util.*;

public class ConnectionPool {

    public ConnectionPool() {
    }


    private final Map<Integer, TCPConnection> hashConnectionMap = new HashMap<>();
    private final Map<String, Set<TCPConnection>> uniqueIDConnectionPool = new HashMap<>();

    public void submitConn(TCPConnection connection) {
        synchronized (hashConnectionMap) {
            hashConnectionMap.put(connection.hashCode(), connection);
        }
    }

    public void submitConn(String uniqueID, TCPConnection connection) {
        if (uniqueID != null && !uniqueID.isEmpty()) {
            synchronized (uniqueIDConnectionPool) {
                Set<TCPConnection> set = uniqueIDConnectionPool.computeIfAbsent(uniqueID, k -> new HashSet<>());
                set.add(connection);
            }
        }
    }

    public Collection<TCPConnection> findConnectionBasedOn(String uniqueID) {
        return uniqueIDConnectionPool.get(uniqueID);
    }

    public TCPConnection findConnectionBasedOn(int hash) {
        return hashConnectionMap.get(hash);
    }

}
