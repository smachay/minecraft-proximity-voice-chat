package com.pvchat.proximityvoicechat.plugin.http;

import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import com.pvchat.proximityvoicechat.plugin.config.ConfigManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class GetChannelHandler implements HttpHandler {
    private final ProximityVoiceChat plugin;

    public GetChannelHandler(ProximityVoiceChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        ConfigManager configManager = plugin.getConfigManager();
        String discordPVCChannelID = configManager.getDiscordPVCChannelID();

        exchange.getResponseHeaders().add("Content-type", "text/plain; charset=UTF-8");

        byte[] response;
        String requestMethod = exchange.getRequestMethod();

        if (!requestMethod.equals("GET")) {
            response = ("Not supported request method: " + requestMethod).getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(405, response.length);
        }else if (discordPVCChannelID != null && discordPVCChannelID.strip().length() > 0) {
            response = discordPVCChannelID.strip().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
        } else {
            response = "Discord proximity voice chat channel ID isn't set in minecraft plugin config (config.yml, attribute \"discordPVCChannelID\" is not set)".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(503, response.length);
        }

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response);
        exchange.close();
    }
}
