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

        ArrayList<PlayerVolumeData> volumeList=playerDistanceAndVolumeCalculations.playerVolumeList();
        if (volumeList!=null){
            for (int i = 0; i < volumeList.size(); i++) {
                PlayerVolumeData temp=volumeList.get(i);
                System.out.println("Player1: "+temp.getPlayer1ID()+" Player2: "+temp.getPlayer2ID()+" Volume: "+temp.getVolumeLevel());

            }
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
