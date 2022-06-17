package com.pvchat.proximityvoicechat.plugin.config.linkmanagers;

import com.pvchat.proximityvoicechat.plugin.config.ConfigManager;
import com.pvchat.proximityvoicechat.plugin.config.DiscordUserID;
import github.scarsz.discordsrv.DiscordSRV;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;


/**
 * Class allows getting minecraft UUID related to given discordID and vice versa. Uses {@link ConfigManager} to link minecraft UUID's and discord ID'.
 */
public class ConfigDiscordLink implements DiscordLink {
    /**
     * Config manager.
     */
    private final ConfigManager configManager;

    public ConfigDiscordLink(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Returns {@link DiscordUserID} related to given minecraft ID if any exists.
     * @param mcID minecraft user UUID
     * @return {@link DiscordUserID} related to given minecraft ID if any exists.
     */
    @Override
    public Optional<DiscordUserID> getDiscordID(UUID mcID) {
        return Optional.ofNullable(configManager.getPlayerLinks().get(mcID));
    }
    /**
     * Returns minecraft user {@link UUID} related to given {@link DiscordUserID} if any exists.
     * @param discordUserID discord user ID
     * @return minecraft user {@link UUID} related to given {@link DiscordUserID} if any exists.
     */
    @Override
    public Optional<UUID> getMinecraftID(DiscordUserID discordUserID) {
        return configManager.getPlayerLinks().entrySet().stream()
                .filter(uuidDiscordUserIDEntry -> uuidDiscordUserIDEntry.getValue().equals(discordUserID))
                .findFirst()
                .map(Map.Entry::getKey);
    }
}
