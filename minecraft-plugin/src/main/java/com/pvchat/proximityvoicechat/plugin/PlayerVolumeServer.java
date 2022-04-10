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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerVolumeServer extends WebSocketServer {

    private final Map<DiscordUserID, WebSocket> openConnections;
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

            if (matrixData == null) return;

            openConnections.forEach((discordUserID, webSocket) -> {
                var optMcUserID = pluginInstance.getDiscordLink().getMinecraftID(discordUserID);
                optMcUserID.ifPresentOrElse(mcUserId -> {
                    var mcUserStringID = mcUserId.toString();
                    ArrayList<PlayerVolumeData> messagePayload = new ArrayList<>();
                    matrixData.forEach(playerVolumeData -> {
                        if (playerVolumeData.getPlayer1ID().equals(mcUserStringID) || playerVolumeData.getPlayer2ID().equals(mcUserStringID)) {
                            messagePayload.add(playerVolumeData);
                        }
                    });
                    logger.info("About to send packet:" + JsonWriter.objectToJson(messagePayload.toArray()));
                    webSocket.send(JsonWriter.objectToJson(messagePayload.toArray()));
                }, () -> logger.log(Level.WARNING, "Open connections list may be corrupt (can't find corresponding MC player UUID)."));
            });
        }
    };

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        try {
            var optDiscordUserId = extractDiscordUserId(clientHandshake);
            optDiscordUserId.ifPresentOrElse(discordUserID -> {
                if (!pluginInstance.getDiscordLink().hasDiscordUser(discordUserID)){
                    sendErrorResponse(webSocket, "Provided discord user ID was not registered.");
                    webSocket.close();
                }
                registerNewConnection(discordUserID, webSocket);
            }, () -> {
                sendErrorResponse(webSocket, "No discord client ID provided (discordID), closing connection.");
                webSocket.close();
            });
        } catch (MalformedURLException e) {
            webSocket.close();
        }
    }
    private void sendErrorResponse(WebSocket webSocket, String message){
        webSocket.send("ERROR: " + message);
        logger.log(Level.WARNING, "Client " +
                "generated error: {0}.", message);
    }

    private Optional<DiscordUserID> extractDiscordUserId(ClientHandshake handshake) throws MalformedURLException {
        URL baseUrl = URL.parse(handshake.getResourceDescriptor());
        Map<String, Collection<String>> queryPars = baseUrl.getQueryPairs();

        if (!queryPars.containsKey(DISCORD_ID_QUERY_KEY)) return Optional.empty();
        return Optional.of(DiscordUserID.parse(queryPars.get(DISCORD_ID_QUERY_KEY).iterator().next()));
    }

    private void registerNewConnection(DiscordUserID discordUserId, WebSocket connection) {
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

}
