package com.pvchat.proximityvoicechat.plugin.config;

public class DiscordUserID {
    private final long value;

    public DiscordUserID(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Long.toUnsignedString(value);
    }

    public static DiscordUserID parse(String id) throws IllegalArgumentException{
        try {
            return new DiscordUserID(Long.parseUnsignedLong(id));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Provided user id is not valid (" + id + ").");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscordUserID that = (DiscordUserID) o;
        return value == that.value;
    }
}
