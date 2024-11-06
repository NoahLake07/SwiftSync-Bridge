package com.swiftsync.bridge.server;

import com.swiftsync.bridge.backend.BasicServer;

public class BridgeServer extends BasicServer {

    public BridgeServer(int port) {
        super(port);

        startServer();
    }

}
