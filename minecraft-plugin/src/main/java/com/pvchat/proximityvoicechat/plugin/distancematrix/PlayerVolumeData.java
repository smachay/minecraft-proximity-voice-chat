package com.pvchat.proximityvoicechat.plugin.distancematrix;

import com.pvchat.proximityvoicechat.plugin.config.DiscordUserID;

/**
 * Stores volume level between two players identified by {@link DiscordUserID}.
 */
public class PlayerVolumeData {
    private final DiscordUserID player1;
    private final DiscordUserID player2;
    private final int volumeLevel;

    /**
     * Creates new {@link PlayerVolumeData} with given parameters.
     *
     * @param player1ID   player1 discord ID
     * @param player2ID   player2 discord ID
     * @param volumeLevel volume level
     */
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
