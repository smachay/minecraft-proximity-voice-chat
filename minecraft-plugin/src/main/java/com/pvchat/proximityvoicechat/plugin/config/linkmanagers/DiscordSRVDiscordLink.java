package com.pvchat.proximityvoicechat.plugin.config.linkmanagers;

import com.pvchat.proximityvoicechat.plugin.config.DiscordUserID;
import github.scarsz.discordsrv.DiscordSRV;

import java.util.Optional;
import java.util.UUID;

/**
 * Class allows getting minecraft UUID related to given discordID and vice versa. Uses {@link DiscordSRV} plugin to link minecraft UUID's and discord ID'.
 */
public class DiscordSRVDiscordLink implements DiscordLink {
    private final DiscordSRV discordSRV;

    public DiscordSRVDiscordLink(DiscordSRV discordSRV) {
        this.discordSRV = discordSRV;
    }

    /**
     * Returns {@link DiscordUserID} related to given minecraft ID if any exists.
     * @param mcID minecraft user UUID
     * @return {@link DiscordUserID} related to given minecraft ID if any exists.
     */
    @Override
    public Optional<DiscordUserID> getDiscordID(UUID mcID) {
        var discordId = discordSRV.getAccountLinkManager().getDiscordId(mcID);
        return Optional.ofNullable(discordId).map(DiscordUserID::parse);
    }

    /**
     * Returns minecraft user {@link UUID} related to given {@link DiscordUserID} if any exists.
     * @param discordUserID discord user ID
     * @return minecraft user {@link UUID} related to given {@link DiscordUserID} if any exists.
     */
    @Override
    public Optional<UUID> getMinecraftID(DiscordUserID discordUserID) {
        var mcId = discordSRV.getAccountLinkManager().getUuid(discordUserID.toString());
        return Optional.ofNullable(mcId);
    }
}
