package com.pvchat.proximityvoicechat.plugin;

import com.anthonynsimon.url.URL;
import com.anthonynsimon.url.exceptions.MalformedURLException;
import org.bukkit.Bukkit;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerVolumeServer extends WebSocketServer {

    private final Map<String /* DC user ID */, WebSocket> openConnections;
    private final Logger logger;
    private final String DISCORD_ID_QUERY_KEY = "discordID";

    public PlayerVolumeServer(int webSocketPort) {
        super(new InetSocketAddress(webSocketPort));
        openConnections = new ConcurrentHashMap<>();
        logger = Bukkit.getLogger();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        try {
            var discordUserId = extractDiscordUserId(clientHandshake);
            if (discordUserId == null) {
                webSocket.send("ERROR: No discord client ID provided (discordID), closing connection.");
                String ip = webSocket.getRemoteSocketAddress().getAddress().toString();
                logger.log(Level.WARNING, "Unknown client ({0}) " +
                        "made incorrect WebSocket request.", ip);
                webSocket.close();
            }
            registerNewConnection(discordUserId, webSocket);
        } catch (MalformedURLException e) {
            webSocket.close();
        }
    }

    private String extractDiscordUserId(ClientHandshake handshake) throws MalformedURLException {
        URL baseUrl = URL.parse(handshake.getResourceDescriptor());
        Map<String, Collection<String>> queryPars = baseUrl.getQueryPairs();

        if (!queryPars.containsKey(DISCORD_ID_QUERY_KEY)) return null;
        if (queryPars.get(DISCORD_ID_QUERY_KEY).isEmpty()) return null;
        return queryPars.get(DISCORD_ID_QUERY_KEY).iterator().next();
    }

    private void registerNewConnection(String discordUserId, WebSocket connection) {
        openConnections.put(discordUserId, connection);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println(openConnections.containsValue(webSocket));
        openConnections.forEach((s1, webSocket1) -> {
            if (webSocket1.equals(webSocket)) {
                openConnections.remove(s1);
            }
        });
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        if (s.equals("getDistanceMatrix")){

        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
    }

    @Override
    public void onStart() {
    }

    private String getDiscordID(WebSocket socket){
         Map.Entry<String, WebSocket> socketEntry = openConnections.entrySet().stream().filter(stringWebSocketEntry ->
                 stringWebSocketEntry.getValue().equals(socket)).findAny().orElse(null);
         if (socketEntry == null) return null;
         return socketEntry.getKey();
    }
}
