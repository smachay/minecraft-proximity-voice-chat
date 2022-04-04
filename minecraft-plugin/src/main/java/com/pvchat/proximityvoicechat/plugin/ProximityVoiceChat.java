package com.pvchat.proximityvoicechat.plugin;

import com.pvchat.proximityvoicechat.plugin.distanceMatrix.PlayerDistanceAndVolumeCalculations;
import com.pvchat.proximityvoicechat.plugin.distanceMatrix.PlayerVolumeData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.function.Consumer;


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

        var socketServer = new PlayerVolumeServer(configManager.getWebSocketPort(), ProximityVoiceChat.instance);
        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                socketServer.run();
            }
        });

        //Load config from config.yml file
        configManager.loadConfig();

        playerDistanceAndVolumeCalculations = new PlayerDistanceAndVolumeCalculations(this);
        playerDistanceAndVolumeCalculations.addStateChangeListener(socketServer.sendPlayerVolumeMatrix);

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
