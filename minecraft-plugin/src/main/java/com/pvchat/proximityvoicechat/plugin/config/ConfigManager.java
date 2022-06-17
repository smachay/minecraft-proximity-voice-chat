package com.pvchat.proximityvoicechat.plugin.config;

import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Panda;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Stores Proximity Voice Chat plugin user configuration.
 */
public class ConfigManager {
    ProximityVoiceChat pluginInstance;
    private int maxHearDistance;
    private int noAttenuationDistance;
    private int webSocketPort;
    private int httpServerPort;
    private String discordPVCChannelID;

    //Player IGN - discord name map
    private Map<UUID, DiscordUserID> playerLinks;

    private GeneralNames serverGeneralNames;

    /**
     * Creates new {@link ConfigManager} instance.
     * @param pluginInstance plugin instance
     */
    public ConfigManager(ProximityVoiceChat pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    /**
     * Loads configuration from Proximity Voice Chat plugin YAML configuration file.
     */
    public void loadConfig() {
        pluginInstance.saveDefaultConfig();
        FileConfiguration config = pluginInstance.getConfig();

        //Load pvc volume settings
        maxHearDistance = config.getInt("maxHearDistance");
        noAttenuationDistance = config.getInt("noAttenuationDistance");
        webSocketPort = config.getInt("webSocketPort");
        httpServerPort = config.getInt("httpServerPort");
        discordPVCChannelID = config.getString("discordPVCChannelID");

        //Load general names
        List<String> strNames = config.getStringList("serverIPAddresses");
        //TODO: remove debug maeesage
        strNames.forEach(System.out::println);

        var ipv4 = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$";
        var generalNames = strNames.stream().map(s -> {
            if (s.matches(ipv4)) return new GeneralName(GeneralName.iPAddress, s);
            else return new GeneralName(GeneralName.dNSName, s);
        }).toArray(GeneralName[]::new);
        serverGeneralNames = new GeneralNames(generalNames);

        //Load links (optional)
        ConfigurationSection section = config.getConfigurationSection("links");
        playerLinks = new HashMap<>();
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            keys.forEach(s -> {
                String value = section.getString(s);
                if (value != null) playerLinks.put(UUID.fromString(s), DiscordUserID.parse(value));
            });
        }
    }

    public int getMaxHearDistance() {
        return maxHearDistance;
    }

    public void setMaxHearDistance(int maxHearDistance) {
        this.maxHearDistance = maxHearDistance;
    }

    public int getNoAttenuationDistance() {
        return noAttenuationDistance;
    }

    public void setNoAttenuationDistance(int noAttenuationDistance) {
        this.noAttenuationDistance = noAttenuationDistance;
    }

    public Map<UUID, DiscordUserID> getPlayerLinks() {
        return playerLinks;
    }

    public void setPlayerLinks(Map<UUID, DiscordUserID> playerLinks) {
        this.playerLinks = playerLinks;
    }

    public int getWebSocketPort() {
        return webSocketPort;
    }

    public int getHttpServerPort() {
        return httpServerPort;
    }

    public void setHttpServerPort(int httpServerPort) {
        this.httpServerPort = httpServerPort;
    }

    public String getDiscordPVCChannelID() {
        return discordPVCChannelID;
    }

    public void setDiscordPVCChannelID(String discordPVCChannelID) {
        this.discordPVCChannelID = discordPVCChannelID;
    }

    public void reload() {
        // If any data was cached by plugin to be saved in config, this is the moment to save all this cached data to config files.
        loadConfig();
    }

    public GeneralNames getServerGeneralNames() {
        return serverGeneralNames;
    }
}
