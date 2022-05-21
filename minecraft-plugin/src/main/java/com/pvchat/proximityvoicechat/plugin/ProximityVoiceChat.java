package com.pvchat.proximityvoicechat.plugin;

import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.ConfigDiscordLink;
import com.pvchat.proximityvoicechat.plugin.config.ConfigManager;
import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.DiscordLink;
import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.DiscordSRVDiscordLink;
import com.pvchat.proximityvoicechat.plugin.distancematrix.PlayerDistanceAndVolumeCalculations;
import com.pvchat.proximityvoicechat.plugin.socket.PlayerVolumeServer;
import com.pvchat.proximityvoicechat.plugin.socket.SSLCertUtils;
import github.scarsz.discordsrv.DiscordSRV;
import org.bouncycastle.operator.OperatorCreationException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


public final class ProximityVoiceChat extends JavaPlugin {
    private ConfigManager configManager;
    private PlayerDistanceAndVolumeCalculations playerDistanceAndVolumeCalculations;
    private static ProximityVoiceChat instance;
    private DiscordLink discordLink;

    private PlayerVolumeServer socketServer;

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


        playerDistanceAndVolumeCalculations = new PlayerDistanceAndVolumeCalculations(this, configManager.getMaxHearDistance(), configManager.getNoAttenuationDistance(), socketServer.sendPlayerVolumeMatrix);

        playerDistanceAndVolumeCalculations.updateVolume(this);


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
        socketServer.stopServer();
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
