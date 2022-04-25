package com.pvchat.proximityvoicechat.plugin;

import com.pvchat.proximityvoicechat.plugin.distanceMatrix.PlayerDistanceAndVolumeCalculations;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public final class ProximityVoiceChat extends JavaPlugin {
    private ConfigManager configManager;
    private PlayerDistanceAndVolumeCalculations playerDistanceAndVolumeCalculations;
    private static ProximityVoiceChat instance;
    private DiscordLink discordLink;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // PluginConfiguration is not used, ConfigManager is used instead
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        instance = this;

        discordLink = createDiscordLink();
        var socketServer = new PlayerVolumeServer(configManager.getWebSocketPort(), ProximityVoiceChat.instance);

        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, socketServer::run);


        playerDistanceAndVolumeCalculations = new PlayerDistanceAndVolumeCalculations(this);
        playerDistanceAndVolumeCalculations.addStateChangeListener(socketServer.sendPlayerVolumeMatrix);

        playerDistanceAndVolumeCalculations.updatePlayerList(this);

    }

    private DiscordLink createDiscordLink(){
        var discordSRV = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");
        if (discordSRV != null) {
            Bukkit.getLogger().info("Setting discordSRV as DiscordLink implementation");
            return new DiscordSRVDiscordLink(discordSRV);
        }
        Bukkit.getLogger().info("Setting ConfigManager as DiscordLink implementation");
        return new ConfigDiscordLink(configManager);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public DiscordLink getDiscordLink() {
        return discordLink;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
