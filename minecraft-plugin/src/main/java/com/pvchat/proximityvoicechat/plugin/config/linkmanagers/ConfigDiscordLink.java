package com.pvchat.proximityvoicechat.plugin.config.linkmanagers;

import com.pvchat.proximityvoicechat.plugin.config.ConfigManager;
import com.pvchat.proximityvoicechat.plugin.config.DiscordUserID;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ConfigDiscordLink implements DiscordLink {
    private final ConfigManager configManager;

    public ConfigDiscordLink(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public Optional<DiscordUserID> getDiscordID(UUID mcID) {
        return Optional.ofNullable(configManager.getPlayerLinks().get(mcID));
    }

    @Override
    public Optional<UUID> getMinecraftID(DiscordUserID discordUserID) {
        return configManager.getPlayerLinks().entrySet().stream()
                .filter(uuidDiscordUserIDEntry -> uuidDiscordUserIDEntry.getValue().equals(discordUserID))
                .findFirst()
                .map(Map.Entry::getKey);
    }
}
