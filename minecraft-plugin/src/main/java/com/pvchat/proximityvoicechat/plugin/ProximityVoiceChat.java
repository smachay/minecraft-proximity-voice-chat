package com.pvchat.proximityvoicechat.plugin;

import com.pvchat.proximityvoicechat.plugin.distanceMatrix.PlayerDistanceAndVolumeCalculations;
import org.bukkit.plugin.java.JavaPlugin;


public final class ProximityVoiceChat extends JavaPlugin {
    private ConfigManager configManager;
    private PlayerDistanceAndVolumeCalculations playerDistanceAndVolumeCalculations;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // PluginConfiguration is not used, ConfigManager is used instead
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        new PlayerVolumeServer(configManager.getWebSocketPort()).run();

        //Load config from config.yml file
        configManager.loadConfig();

        playerDistanceAndVolumeCalculations = new PlayerDistanceAndVolumeCalculations(this);

        playerDistanceAndVolumeCalculations.updatePlayerList();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
