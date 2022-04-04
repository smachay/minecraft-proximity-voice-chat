package com.pvchat.proximityvoicechat.plugin;

import com.pvchat.proximityvoicechat.plugin.distanceMatrix.PlayerDistanceAndVolumeCalculations;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public final class ProximityVoiceChat extends JavaPlugin {
    private ConfigManager configManager;
    private PlayerDistanceAndVolumeCalculations playerDistanceAndVolumeCalculations;
    private static ProximityVoiceChat instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // PluginConfiguration is not used, ConfigManager is used instead
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        instance = this;

        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                new PlayerVolumeServer(configManager.getWebSocketPort(), ProximityVoiceChat.instance).run();
            }
        });

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
