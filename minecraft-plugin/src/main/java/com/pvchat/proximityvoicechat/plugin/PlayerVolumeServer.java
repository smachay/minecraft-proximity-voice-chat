package com.pvchat.proximityvoicechat.plugin;

import com.anthonynsimon.url.URL;
import com.anthonynsimon.url.exceptions.MalformedURLException;
import com.cedarsoftware.util.io.JsonWriter;
import com.pvchat.proximityvoicechat.plugin.distanceMatrix.PlayerVolumeData;
import org.bukkit.Bukkit;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerVolumeServer extends WebSocketServer {

    private final Map<String /* DC user ID */, WebSocket> openConnections;
    private final Logger logger;
    private final String DISCORD_ID_QUERY_KEY = "discordID";
    private final ProximityVoiceChat pluginInstance;

    public PlayerVolumeServer(int webSocketPort, ProximityVoiceChat pluginInstance) {
        super(new InetSocketAddress(webSocketPort));
        this.pluginInstance = pluginInstance;
        openConnections = new ConcurrentHashMap<>();
        logger = Bukkit.getLogger();
    }

    public Consumer<List<PlayerVolumeData>> sendPlayerVolumeMatrix = new Consumer<>() {
        @Override
        public void accept(List<PlayerVolumeData> matrixData) {
            var playerLinks = pluginInstance.getConfigManager().getPlayerLinks();

            if (matrixData == null) return;

            openConnections.forEach((s, webSocket) -> {
                var sLink = playerLinks.entrySet().stream().filter(uuidStringEntry -> uuidStringEntry.getValue().equals(s)).findAny().orElse(null);
                if (sLink == null) {
                    logger.log(Level.WARNING, "Open connections list may be corrupt (can't find corresponding MC player UUID).");
                    return;
                }
                String playerUUID = sLink.getKey().toString();
                ArrayList<PlayerVolumeData> messagePayload = new ArrayList<>();
                matrixData.forEach(playerVolumeData -> {
                    if (playerVolumeData.getPlayer1ID().equals(playerUUID) || playerVolumeData.getPlayer2ID().equals(playerUUID)) {
                        messagePayload.add(playerVolumeData);
                    }
                });
                logger.info("About to send packet:" + JsonWriter.objectToJson(messagePayload.toArray()));
                webSocket.send(JsonWriter.objectToJson(messagePayload.toArray()));
            });
        }
    };

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        try {
            var discordUserId = extractDiscordUserId(clientHandshake);
            if (discordUserId == null) {
                sendErrorResponse(webSocket, "No discord client ID provided (discordID), closing connection.");
                webSocket.close();
            }
            if (!pluginInstance.getConfigManager().getPlayerLinks().containsValue(discordUserId)){
                sendErrorResponse(webSocket, "Provided discord user ID was not registered.");
                webSocket.close();
            }
            registerNewConnection(discordUserId, webSocket);
        } catch (MalformedURLException e) {
            webSocket.close();
        }
    }
    private void sendErrorResponse(WebSocket webSocket, String message){
        webSocket.send("ERROR: " + message);
        logger.log(Level.WARNING, "Client " +
                "generated error: {0}.", message);
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
