package com.pvchat.proximityvoicechat.plugin.http;

import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import com.pvchat.proximityvoicechat.plugin.socket.SSLCertUtils;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * HTTPS server which is hosting one "/channel" endpoint with discord PVC channel ID - the discord channel ID on which Proximity Voice Chat works.
 */
public class PVCHttpsServer {
    private final ProximityVoiceChat plugin;
    private final HttpsServer server;

    /**
     * Creates new {@link PVCHttpsServer} instance.
     *
     * @param plugin   plugin instance.
     * @param hostname hostname.
     * @param port     port.
     * @throws IOException
     */
    public PVCHttpsServer(ProximityVoiceChat plugin, String hostname, int port) throws IOException {
        this.plugin = plugin;
        server = HttpsServer.create(new InetSocketAddress(hostname, port), 0);
        configureHttpsServer();
        createContext();
    }

    /**
     * Creates new {@link PVCHttpsServer} instance with default hostname.
     *
     * @param plugin plugin instance.
     * @param port   port.
     * @throws IOException
     */
    public PVCHttpsServer(ProximityVoiceChat plugin, int port) throws IOException {
        this.plugin = plugin;
        server = HttpsServer.create(new InetSocketAddress(port), 0);
        configureHttpsServer();
        createContext();
    }

    /**
     * Configures HTTPS server.
     */
    private void configureHttpsServer() {
        server.setHttpsConfigurator(new HttpsConfigurator(SSLCertUtils.getDefaultSSLContext()) {
            public void configure(HttpsParameters params) {
                params.setNeedClientAuth(false);
                params.setSSLParameters(getSSLContext().getDefaultSSLParameters());
            }
        });
    }

    /**
     * Creates endpoints.
     */
    private void createContext() {
        server.createContext("/channel", new GetChannelHandler(plugin)); // returns discord proximity voice chat channel id

    }

    /**
     * Starts HTTPS server.
     */
    public void start() {
        server.start();
    }

    /**
     * Stops HTTPS server.
     *
     * @param delay server stop delay.
     */
    public void stop(int delay) {
        server.stop(delay);
    }
}
