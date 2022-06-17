package com.pvchat.proximityvoicechat.plugin.config;

/**
 * Represents Discord user ID.
 */
public class DiscordUserID {
    private final long value;

    /**
     * Creates new instance of {@link DiscordUserID} from given long value.
     * @param value discord user id.
     */
    public DiscordUserID(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Long.toUnsignedString(value);
    }

    /**
     * Parses given discord id from string and creates new {@link DiscordUserID} basing on it.
     * @param id discord user id.
     * @return new {@link DiscordUserID} basing on given discord user id.
     * @throws IllegalArgumentException
     */
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
