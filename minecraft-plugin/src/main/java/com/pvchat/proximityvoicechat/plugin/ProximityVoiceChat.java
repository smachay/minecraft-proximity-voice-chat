package com.pvchat.proximityvoicechat.plugin;

import com.pvchat.proximityvoicechat.plugin.distanceMatrix.PlayerDistanceMatrix;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProximityVoiceChat extends JavaPlugin {
    private ConfigManager configManager;
    private PlayerDistanceMatrix playerDistanceMatrix;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // PluginConfiguration is not used, ConfigManager is used instead
        configManager = new ConfigManager(this);

        //Load config from config.yml file
        configManager.loadConfig();

        playerDistanceMatrix = new PlayerDistanceMatrix(this);

        playerDistanceMatrix.updateMatrix();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
