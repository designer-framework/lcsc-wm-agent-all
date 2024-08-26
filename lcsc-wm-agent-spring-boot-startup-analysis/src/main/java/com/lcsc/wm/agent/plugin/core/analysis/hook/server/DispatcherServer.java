package com.lcsc.wm.agent.plugin.core.analysis.hook.server;

import java.io.IOException;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-09 00:45
 */
public class DispatcherServer {

    private final JDKHttpServer server;

    public DispatcherServer(int port, HandlerMapping handlerMapping) throws IOException {
        this.server = new JDKHttpServer(port, handlerMapping);
    }

    public void start() {
        server.start();
    }

}
