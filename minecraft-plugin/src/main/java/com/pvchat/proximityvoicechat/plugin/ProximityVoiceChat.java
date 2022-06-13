package com.pvchat.proximityvoicechat.plugin;

import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.ConfigDiscordLink;
import com.pvchat.proximityvoicechat.plugin.config.ConfigManager;
import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.DiscordLink;
import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.DiscordSRVDiscordLink;
import com.pvchat.proximityvoicechat.plugin.distancematrix.PlayerDistanceAndVolumeCalculations;
import com.pvchat.proximityvoicechat.plugin.http.PVCHttpsServer;
import com.pvchat.proximityvoicechat.plugin.socket.PlayerVolumeServer;
import com.pvchat.proximityvoicechat.plugin.socket.SSLCertUtils;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;


public final class ProximityVoiceChat extends JavaPlugin {
    private ConfigManager configManager;
    private PlayerDistanceAndVolumeCalculations playerDistanceAndVolumeCalculations;
    private static ProximityVoiceChat instance;
    private DiscordLink discordLink;

    private PlayerVolumeServer socketServer;
    private PVCHttpsServer httpServer;

    private Logger logger;

    @Override
    public void onEnable() {
        try {
            logger = getLogger();
            // Plugin startup logic
            // PluginConfiguration is not used, ConfigManager is used instead
            configManager = new ConfigManager(this);
            configManager.loadConfig();

            SSLCertUtils.verifyKeyStorePresent(logger, configManager.getServerGeneralNames());

            instance = this;

            discordLink = createDiscordLink();
            socketServer = new PlayerVolumeServer(configManager.getWebSocketPort(), ProximityVoiceChat.instance);


            Bukkit.getScheduler().scheduleAsyncDelayedTask(this, socketServer::run);

            try {
                httpServer = new PVCHttpsServer(this, configManager.getHttpServerPort());
                Bukkit.getScheduler().scheduleAsyncDelayedTask(this, httpServer::start);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error starting http server. Might be port conflict.");
                e.printStackTrace();
            }

            playerDistanceAndVolumeCalculations = new PlayerDistanceAndVolumeCalculations(this, configManager.getMaxHearDistance(), configManager.getNoAttenuationDistance(), socketServer.sendPlayerVolumeMatrix);

            playerDistanceAndVolumeCalculations.updateVolume(this);

        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "An error occurred during initialization of the plugin.");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private DiscordLink createDiscordLink() {
        var discordSRV = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");
        if (discordSRV != null) {
            logger.info("Setting discordSRV as DiscordLink implementation");
            return new DiscordSRVDiscordLink(discordSRV);
        }
        logger.info("Setting ConfigManager as DiscordLink implementation");
        return new ConfigDiscordLink(configManager);
    }

    @Override
    public void onDisable() {
        if (socketServer != null) socketServer.stopServer();
        if (httpServer != null) httpServer.stop(0);
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
}
