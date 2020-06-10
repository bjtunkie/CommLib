package com.commlib.v1.comm;

import java.util.*;
import java.util.function.Predicate;

public final class ConnectionPool {

    ConnectionPool() {
    }

    private final Collection<TCPConnection> connections = new HashSet<>();
    private final Map<String, Set<TCPConnection>> uniqueIDConnectionPool = new HashMap<>();

    public void addConnection(TCPConnection conn) {
        connections.add(conn);
    }

    public void submitConn(String uniqueID, TCPConnection connection) {
        if (uniqueID != null && !uniqueID.isEmpty()) {
            synchronized (uniqueIDConnectionPool) {
                Set<TCPConnection> set = uniqueIDConnectionPool.computeIfAbsent(uniqueID, k -> new HashSet<>());
                set.add(connection);
            }
        }
        connections.add(connection);
    }


    public Collection<TCPConnection> findConnectionBasedOn(String uniqueID) {
        return uniqueIDConnectionPool.computeIfAbsent(uniqueID, k -> new HashSet<>());
    }

    public Collection<TCPConnection> getConnections() {
        return connections;
    }

    private final Predicate<TCPConnection> p = c -> !c.isOpen();

    public void removeClosedConnections() {
        connections.removeIf(p);
        uniqueIDConnectionPool
                .values()
                .parallelStream()
                .forEach(x -> {
                    x.removeIf(p);
                });
    }


}
