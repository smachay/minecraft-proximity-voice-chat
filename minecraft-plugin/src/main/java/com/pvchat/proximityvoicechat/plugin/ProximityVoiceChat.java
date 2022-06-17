package com.pvchat.proximityvoicechat.plugin;

import com.pvchat.proximityvoicechat.plugin.commands.MainCommandExecutor;
import com.pvchat.proximityvoicechat.plugin.config.ConfigManager;
import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.ConfigDiscordLink;
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

/**
 * Plugin main class, responsible for setting up and disabling ProximityVoiceChat plugin.
 */
public final class ProximityVoiceChat extends JavaPlugin {
    private ConfigManager configManager;
    private PlayerDistanceAndVolumeCalculations playerDistanceAndVolumeCalculations;
    public static ProximityVoiceChat instance;
    private DiscordLink discordLink;

    private PlayerVolumeServer socketServer;
    private PVCHttpsServer httpServer;

    private Logger logger;

    /**
     * Plugin startup method.
     * Sets up config manager and discord link.
     * Turns on socket server and https server.
     * Registers plugins commands.
     */
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

            //Register commands
            var pvcCommand = getCommand("pvc");
            var mainCommandExecutor = new MainCommandExecutor(this);
            pvcCommand.setExecutor(mainCommandExecutor);
            pvcCommand.setTabCompleter(mainCommandExecutor);
            playerDistanceAndVolumeCalculations = new PlayerDistanceAndVolumeCalculations(this, configManager.getMaxHearDistance(), configManager.getNoAttenuationDistance(), socketServer.getSendPlayerVolumeMatrix());

            playerDistanceAndVolumeCalculations.updateVolume(this);

        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "An error occurred during initialization of the plugin.");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Checks if discordSRV plugin is set up on the server and sets new {@link DiscordLink} basing on that information
     *
     * @return {@link DiscordSRVDiscordLink} if discordSRV plugin is set up on the server, otherwise returns new {@link ConfigDiscordLink}
     */
    private DiscordLink createDiscordLink() {
        var discordSRV = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");
        if (discordSRV != null) {
            logger.info("Setting discordSRV as DiscordLink implementation");
            return new DiscordSRVDiscordLink(discordSRV);
        }
        logger.info("Setting ConfigManager as DiscordLink implementation");
        return new ConfigDiscordLink(configManager);
    }

    /**
     * Plugin shutdown method. Stops socket server and http server.
     */
    @Override
    public void onDisable() {
        if (socketServer != null) socketServer.stopServer();
        if (httpServer != null) httpServer.stop(0);
    }

    /**
     * Returns {@link DiscordLink}
     *
     * @return {@link DiscordLink}
     */
    public DiscordLink getDiscordLink() {
        return discordLink;
    }

    /**
     * Returns {@link PlayerDistanceAndVolumeCalculations}
     *
     * @return {@link PlayerDistanceAndVolumeCalculations}
     */
    public PlayerDistanceAndVolumeCalculations getPlayerDistanceAndVolumeCalculations() {
        return playerDistanceAndVolumeCalculations;
    }

    /**
     * Returns {@link ConfigManager}
     *
     * @return {@link ConfigManager}
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Returns {@link PlayerVolumeServer}
     *
     * @return {@link PlayerVolumeServer}
     */
    public PlayerVolumeServer getSocketServer() {
        return socketServer;
    }
}
