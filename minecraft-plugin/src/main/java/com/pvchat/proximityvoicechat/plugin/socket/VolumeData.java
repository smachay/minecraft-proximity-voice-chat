package com.pvchat.proximityvoicechat.plugin.socket;

/**
 * Stores volume level between players.
 */
public record VolumeData(String player1, String player2, int volume) {
}
