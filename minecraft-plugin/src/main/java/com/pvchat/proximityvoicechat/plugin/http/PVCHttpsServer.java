package com.pvchat.proximityvoicechat.plugin.http;

import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import com.pvchat.proximityvoicechat.plugin.socket.SSLCertUtils;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PVCHttpsServer {
    private final ProximityVoiceChat plugin;
    private final HttpsServer server;

    public PVCHttpsServer(ProximityVoiceChat plugin, String hostname, int port) throws IOException {
        this.plugin = plugin;
        server = HttpsServer.create(new InetSocketAddress(hostname, port), 0);
        configureHttpsServer();
        createContext();
    }

    public PVCHttpsServer(ProximityVoiceChat plugin, int port) throws IOException {
        this.plugin = plugin;
        server = HttpsServer.create(new InetSocketAddress(port), 0);
        configureHttpsServer();
        createContext();
    }

    private void configureHttpsServer() {
        server.setHttpsConfigurator(new HttpsConfigurator(SSLCertUtils.getDefaultSSLContext()){
            public void configure(HttpsParameters params){
                params.setNeedClientAuth(false);
                params.setSSLParameters(getSSLContext().getDefaultSSLParameters());
            }
        });
    }
    private void createContext(){
        server.createContext("/channel", new GetChannelHandler(plugin)); // returns discord proximity voice chat channel id

    }
    public void start() {
        server.start();
    }

    public void stop(int delay) {
        server.stop(delay);
    }
}
