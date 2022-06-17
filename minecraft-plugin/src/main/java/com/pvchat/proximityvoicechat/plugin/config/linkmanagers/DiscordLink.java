package com.pvchat.proximityvoicechat.plugin.config.linkmanagers;

import com.pvchat.proximityvoicechat.plugin.config.DiscordUserID;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface allows getting minecraft UUID related to given discordID and vice versa.
 */
public interface DiscordLink {
    /**
     * Returns {@link DiscordUserID} related to given minecraft ID if any exists.
     * @param mcID minecraft user UUID
     * @return {@link DiscordUserID} related to given minecraft ID if any exists.
     */
    Optional<DiscordUserID> getDiscordID(UUID mcID);
    /**
     * Returns minecraft user {@link UUID} related to given {@link DiscordUserID} if any exists.
     * @param discordUserID discord user ID
     * @return minecraft user {@link UUID} related to given {@link DiscordUserID} if any exists.
     */
    Optional<UUID> getMinecraftID(DiscordUserID discordUserID);

    /**
     * Tells if given {@link DiscordUserID} is linked to minecraft user {@link UUID}.
     * @param id discord user ID
     * @return true if given discord user ID is linked to minecraft user UUID, false otherwise.
     */
    default boolean hasDiscordUser(DiscordUserID id) {
        var mcId = getMinecraftID(id);
        return mcId.isPresent();
    }
}
