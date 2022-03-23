package com.pvchat.proximityvoicechat.plugin.distanceMatrix;

public class PlayerVolumeData {
 private String player1ID;
 private String player2ID;
 private int volumeLevel;

    public PlayerVolumeData(String player1ID, String player2ID, int volumeLevel) {
        this.player1ID = player1ID;
        this.player2ID = player2ID;
        this.volumeLevel = volumeLevel;
    }

    public String getPlayer1ID() {
        return player1ID;
    }

    public void setPlayer1ID(String player1ID) {
        this.player1ID = player1ID;
    }

    public String getPlayer2ID() {
        return player2ID;
    }

    public void setPlayer2ID(String player2ID) {
        this.player2ID = player2ID;
    }

    public int getVolumeLevel() {
        return volumeLevel;
    }

    public void setVolumeLevel(int volumeLevel) {
        this.volumeLevel = volumeLevel;
    }
}
