package com.pvchat.proximityvoicechat.plugin;

import com.pvchat.proximityvoicechat.plugin.distanceMatrix.PlayerDistanceAndVolumeCalculations;
import com.pvchat.proximityvoicechat.plugin.distanceMatrix.PlayerVolumeData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class ProximityVoiceChat extends JavaPlugin {
    private ConfigManager configManager;
    private PlayerDistanceAndVolumeCalculations playerDistanceAndVolumeCalculations;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // PluginConfiguration is not used, ConfigManager is used instead
        configManager = new ConfigManager(this);

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
