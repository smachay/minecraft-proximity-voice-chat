package com.pvchat.proximityvoicechat.plugin;

import github.scarsz.discordsrv.DiscordSRV;

import java.util.Optional;
import java.util.UUID;

public class DiscordSRVDiscordLink implements DiscordLink {
    private final DiscordSRV discordSRV;

    public DiscordSRVDiscordLink(DiscordSRV discordSRV) {
        this.discordSRV = discordSRV;
    }

    @Override
    public Optional<DiscordUserID> getDiscordID(UUID mcID) {
        var discordId = discordSRV.getAccountLinkManager().getDiscordId(mcID);
        return Optional.ofNullable(discordId).map(DiscordUserID::parse);
    }

    @Override
    public Optional<UUID> getMinecraftID(DiscordUserID discordUserID) {
        var mcId = discordSRV.getAccountLinkManager().getUuid(discordUserID.toString());
        return Optional.ofNullable(mcId);
    }
}
