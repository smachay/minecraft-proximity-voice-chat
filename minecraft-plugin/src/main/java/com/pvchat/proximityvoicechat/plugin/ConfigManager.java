package com.pvchat.proximityvoicechat.plugin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class ConfigManager {
    ProximityVoiceChat pluginInstance;
    private int maxHearDistance;
    private int noAttenuationDistance;
    private float linearAttenuationFactor;

    //Player IGN - discord name map
    private HashMap<UUID, String> playerLinks;

    public ConfigManager(ProximityVoiceChat pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    public void loadConfig() {
        pluginInstance.saveDefaultConfig();
        FileConfiguration config = pluginInstance.getConfig();
        maxHearDistance = config.getInt("defaultMaxHearDistance");
        noAttenuationDistance = config.getInt("defaultNoAttenuationDistance");
        linearAttenuationFactor = (float) config.getDouble("defaultLinearAttenuationFactor");
        ConfigurationSection section = config.getConfigurationSection("links");
        playerLinks = new HashMap<>();
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            keys.forEach(s -> {
                String value = section.getString(s);
                if (value != null) playerLinks.put(UUID.fromString(s), value);

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

    public float getLinearAttenuationFactor() {
        return linearAttenuationFactor;
    }

    public void setLinearAttenuationFactor(float linearAttenuationFactor) {
        this.linearAttenuationFactor = linearAttenuationFactor;
    }

    public HashMap<UUID, String> getPlayerLinks() {
        return playerLinks;
    }

    public void setPlayerLinks(HashMap<UUID, String> playerLinks) {
        this.playerLinks = playerLinks;
    }

}
