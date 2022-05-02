package com.pvchat.proximityvoicechat.plugin.http;

import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PVCHttpServer {

    private final HttpServer server;

    public PVCHttpServer(ProximityVoiceChat plugin, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/channel", new GetChannelHandler(plugin)); // returns discord proximity voice chat channel id
    }

    public void start() {
        server.start();
    }

    public void stop(int delay) {
        server.stop(delay);
    }
}
