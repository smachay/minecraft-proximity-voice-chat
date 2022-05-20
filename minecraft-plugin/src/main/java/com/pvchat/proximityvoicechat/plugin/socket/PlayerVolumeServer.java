package com.pvchat.proximityvoicechat.plugin.socket;

import com.anthonynsimon.url.URL;
import com.anthonynsimon.url.exceptions.MalformedURLException;
import com.cedarsoftware.util.io.JsonWriter;
import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.DiscordLink;
import com.pvchat.proximityvoicechat.plugin.config.DiscordUserID;
import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import com.pvchat.proximityvoicechat.plugin.distancematrix.PlayerDistanceAndVolumeCalculations;
import com.pvchat.proximityvoicechat.plugin.distancematrix.PlayerVolumeData;
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
    private static final String DISCORD_ID_QUERY_KEY = "discordID";
    private final ProximityVoiceChat pluginInstance;
    private final DiscordLink discordLink;
    public final Consumer<List<PlayerVolumeData>> sendPlayerVolumeMatrix;

    public PlayerVolumeServer(int webSocketPort, ProximityVoiceChat pluginInstance) {
        super(new InetSocketAddress(webSocketPort));
        this.pluginInstance = pluginInstance;
        discordLink = pluginInstance.getDiscordLink();
        openConnections = new ConcurrentHashMap<>();
        logger = Bukkit.getLogger();
        sendPlayerVolumeMatrix = this::sendVolumeData;
    }

    public void stopServer() {
        try {
            stop();
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Could not stop socket server, was it even running?");
            Thread.currentThread().interrupt();
        }
    }

    public final void sendVolumeData(List<PlayerVolumeData> matrixData) {
        openConnections.forEach((discordUserID, webSocket) -> {
            if(discordLink.hasDiscordUser(discordUserID)){
                ArrayList<VolumeData> messagePayload = new ArrayList<>(openConnections.size());
                PlayerDistanceAndVolumeCalculations
                        .filterPlayerVolumeData(matrixData, discordUserID)
                        .forEach(playerVolumeData -> messagePayload.add(
                                new VolumeData(
                                        playerVolumeData.getPlayer1().toString(),
                                        playerVolumeData.getPlayer2().toString(),
                                        playerVolumeData.getVolumeLevel())
                                )
                        );
                if(!messagePayload.isEmpty()) {
                    String socketMessage = JsonWriter.objectToJson(new DataMessage(messagePayload.toArray(new VolumeData[0])));
                    webSocket.send(socketMessage);
                }
            } else logger.log(Level.WARNING, String.format("Open connections list may be corrupt (can't find corresponding MC player UUID for discord ID : %s", discordUserID.toString()));
        });
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        try {
            var optDiscordUserId = extractDiscordUserId(clientHandshake);
            optDiscordUserId.ifPresentOrElse(discordUserID -> {
                if (!pluginInstance.getDiscordLink().hasDiscordUser(discordUserID)){
                    sendErrorMessage(webSocket, "Provided discord user ID was not registered.");
                    webSocket.close();
                }
                registerNewConnection(discordUserID, webSocket);
            }, () -> {
                sendErrorMessage(webSocket, "No discord client ID provided (discordID), closing connection.");
                webSocket.close();
            });
        } catch (MalformedURLException | IllegalArgumentException e) {
            sendErrorMessage(webSocket, e.getMessage());
            webSocket.close();
        }
    }
    private void sendErrorMessage(WebSocket webSocket, String message){
        webSocket.send(JsonWriter.objectToJson(new ErrorMessage(message)));
        logger.log(Level.WARNING, "Client " +
                "generated error: {0}.", message);
    }

    private Optional<DiscordUserID> extractDiscordUserId(ClientHandshake handshake) throws MalformedURLException, IllegalArgumentException {
        URL baseUrl = URL.parse(handshake.getResourceDescriptor());
        Map<String, Collection<String>> queryPars = baseUrl.getQueryPairs();

        if (!queryPars.containsKey(DISCORD_ID_QUERY_KEY)) return Optional.empty();
        return Optional.of(DiscordUserID.parse(queryPars.get(DISCORD_ID_QUERY_KEY).iterator().next()));
    }

    private void registerNewConnection(DiscordUserID discordUserId, WebSocket connection) {
        if (openConnections.containsKey(discordUserId)) {
            openConnections.compute(discordUserId, (i, j) -> {
                if (j != null) {
                    sendErrorMessage(j, "The same discord id opened new connection, closing this one.");
                    j.close();
                }
                return connection;
            });
        }
        openConnections.put(discordUserId, connection);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        openConnections.forEach((s1, webSocket1) -> {
            if (webSocket1.equals(webSocket)) {
                openConnections.remove(s1);
            }
        });
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        //TODO: Handle message event
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        //TODO: Handle socket error
    }

    @Override
    public void onStart() {
        //TODO: Handle socket start
    }

}
