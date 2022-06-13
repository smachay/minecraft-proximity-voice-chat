package com.pvchat.proximityvoicechat.plugin;

import com.pvchat.proximityvoicechat.plugin.commands.MainCommandExecutor;
import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.ConfigDiscordLink;
import com.pvchat.proximityvoicechat.plugin.config.ConfigManager;
import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.DiscordLink;
import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.DiscordSRVDiscordLink;
import com.pvchat.proximityvoicechat.plugin.distancematrix.PlayerDistanceAndVolumeCalculations;
import com.pvchat.proximityvoicechat.plugin.http.PVCHttpsServer;
import com.pvchat.proximityvoicechat.plugin.socket.PlayerVolumeServer;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

import java.util.logging.Level;


public final class ProximityVoiceChat extends JavaPlugin {
    private ConfigManager configManager;
    private PlayerDistanceAndVolumeCalculations playerDistanceAndVolumeCalculations;
    public static ProximityVoiceChat instance;
    private DiscordLink discordLink;

    private PlayerVolumeServer socketServer;
    private PVCHttpsServer httpServer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // PluginConfiguration is not used, ConfigManager is used instead
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        instance = this;

        discordLink = createDiscordLink();
        socketServer = new PlayerVolumeServer(configManager.getWebSocketPort(), ProximityVoiceChat.instance);

        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, socketServer::run);

        try {
            httpServer = new PVCHttpsServer(this, configManager.getHttpServerPort());
            Bukkit.getScheduler().scheduleAsyncDelayedTask(this, httpServer::start);
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Error starting http server. Might be port conflict.");
            e.printStackTrace();
        }

        playerDistanceAndVolumeCalculations = new PlayerDistanceAndVolumeCalculations(
                this,
                configManager.getMaxHearDistance(),
                configManager.getNoAttenuationDistance(),
                socketServer.getSendPlayerVolumeMatrix()
        );

        playerDistanceAndVolumeCalculations.updateVolume(this);

        //Register commands
        var pvcCommand = getCommand("pvc");
        var mainCommandExecutor = new MainCommandExecutor(this);
        pvcCommand.setExecutor(mainCommandExecutor);
        pvcCommand.setTabCompleter(mainCommandExecutor);

    }

    private DiscordLink createDiscordLink() {
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
        socketServer.stopServer();
        httpServer.stop(0);
    }

    public DiscordLink getDiscordLink() {
        return discordLink;
    }

    public PlayerDistanceAndVolumeCalculations getPlayerDistanceAndVolumeCalculations() {
        return playerDistanceAndVolumeCalculations;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerVolumeServer getSocketServer() {
        return socketServer;
    }
}
