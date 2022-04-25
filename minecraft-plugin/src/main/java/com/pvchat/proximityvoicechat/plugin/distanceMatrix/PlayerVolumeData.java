package com.pvchat.proximityvoicechat.plugin.distanceMatrix;

import com.pvchat.proximityvoicechat.plugin.DiscordUserID;

public class PlayerVolumeData {
 private DiscordUserID player1;
 private DiscordUserID player2;
 private long volumeLevel;

    public PlayerVolumeData(DiscordUserID player1ID, DiscordUserID player2ID, long volumeLevel) {
        this.player1 = player1ID;
        this.player2 = player2ID;
        this.volumeLevel = volumeLevel;
    }

    public DiscordUserID getPlayer1() {
        return player1;
    }

    public void setPlayer1(DiscordUserID player1) {
        this.player1 = player1;
    }

    public DiscordUserID getPlayer2() {
        return player2;
    }

    public void setPlayer2(DiscordUserID player2) {
        this.player2 = player2;
    }

    public long getVolumeLevel() {
        return volumeLevel;
    }

    public void setVolumeLevel(long volumeLevel) {
        this.volumeLevel = volumeLevel;
    }
}
