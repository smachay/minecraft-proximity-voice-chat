package com.pvchat.proximityvoicechat.plugin;

import java.util.Optional;
import java.util.UUID;

public interface DiscordLink {
    Optional<DiscordUserID> getDiscordID(UUID mcID);
    Optional<UUID> getMinecraftID(DiscordUserID discordUserID);
    default boolean hasDiscordUser(DiscordUserID id) {
        var mcId = getMinecraftID(id);
        return mcId.isPresent();
    }
}
