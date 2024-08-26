/*
 * Copyright The async-profiler authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.lcsc.wm.agent.plugin.core.analysis.hook.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class JDKHttpServer extends Thread implements Executor, HttpHandler {

    private final HttpServer server;

    private final AtomicInteger threadNum = new AtomicInteger();

    private final HandlerMapping handlerMapping;

    public JDKHttpServer(int port, HandlerMapping handlerMapping) throws IOException {
        super("Spring-Agent-Web-Server");
        setDaemon(true);

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this);
        server.setExecutor(this);

        this.handlerMapping = handlerMapping;
    }

    @Override
    public void run() {
        server.start();
        log.error("Performance analysis API server: http://127.0.0.1:{}", server.getAddress().getPort());
    }

    @Override
    public void execute(Runnable requestRunnable) {
        Thread t = new Thread(requestRunnable, "Agent Request #" + threadNum.incrementAndGet());
        t.setDaemon(false);
        t.start();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            //URI
            URI requestURI = exchange.getRequestURI();
            Handler handler = handlerMapping.getHandler(requestURI.getPath());

            //JSON, 200
            if (handler != null) {
                //
                handler.handler(exchange);
                //404
            } else {
                sendResponse(exchange, 404, "404 Not Fount");
            }

        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendResponse(exchange, 500, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bodyBytes.length);
        exchange.getResponseBody().write(bodyBytes);
    }

}
