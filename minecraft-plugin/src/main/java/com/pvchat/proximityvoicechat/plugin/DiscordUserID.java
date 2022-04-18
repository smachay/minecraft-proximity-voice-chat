package com.pvchat.proximityvoicechat.plugin;

public class DiscordUserID {
    private final long value;

    public DiscordUserID(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Long.toUnsignedString(value);
    }

    public static DiscordUserID parse(String id) {
        try {
            return new DiscordUserID(Long.parseUnsignedLong(id));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Provided user id is not valid (" + id + ").");
        }
    }
}
