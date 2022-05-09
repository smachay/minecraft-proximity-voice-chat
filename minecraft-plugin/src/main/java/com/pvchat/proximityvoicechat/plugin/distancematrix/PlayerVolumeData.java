package com.pvchat.proximityvoicechat.plugin.distancematrix;

import com.pvchat.proximityvoicechat.plugin.config.DiscordUserID;

public class PlayerVolumeData {
 private final DiscordUserID player1;
 private final DiscordUserID player2;
 private final int volumeLevel;

    public PlayerVolumeData(DiscordUserID player1ID, DiscordUserID player2ID, int volumeLevel) {
        this.player1 = player1ID;
        this.player2 = player2ID;
        this.volumeLevel = volumeLevel;
    }

    public DiscordUserID getPlayer1() {
        return player1;
    }

    public DiscordUserID getPlayer2() {
        return player2;
    }

    public int getVolumeLevel() {
        return volumeLevel;
    }

}
