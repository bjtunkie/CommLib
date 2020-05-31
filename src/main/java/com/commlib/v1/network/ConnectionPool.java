package com.commlib.v1.network;

import java.util.*;

public class ConnectionPool {

    public ConnectionPool() {
    }

    /**
     * Why Hash and IP are maintained in different
     */

    private final Map<Integer, TCPConnection> hashConnectionMap = new HashMap<>();
    private final Map<Integer, TCPConnection> ipConnectionMap = new HashMap<>();
    private final Map<String, Set<TCPConnection>> uniqueIDConnectionPool = new HashMap<>();

    public void submitConn(TCPConnection connection) {
        synchronized (hashConnectionMap) {
            hashConnectionMap.put(connection.hashCode(), connection);
        }
        synchronized (ipConnectionMap) {
            ipConnectionMap.put(connection.getAddressAsInt(), connection);
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

    public TCPConnection findConnectionBasedOnIP(int ip) {
        return ipConnectionMap.get(ip);
    }

    public TCPConnection findConnectionBasedOnIP(String ip) {
        // Parse IP parts into an int array
        byte[] addr = new byte[4];
        String[] parts = ip.split("\\.");

        for (int i = 0; i < 4; i++) {
            addr[i] = (byte) Integer.parseInt(parts[i]);
        }
        int ipAddr = ((addr[0] & 0xFF) << 24) | ((addr[1] & 0xFF) << 16) | ((addr[2] & 0xFF) << 8) | ((addr[3] & 0xFF));
        return findConnectionBasedOnIP(ipAddr);
    }


    public Collection<TCPConnection> findConnectionBasedOn(String uniqueID) {
        return uniqueIDConnectionPool.get(uniqueID);
    }

    public TCPConnection findConnectionBasedOn(int hash) {
        return hashConnectionMap.get(hash);
    }

}
