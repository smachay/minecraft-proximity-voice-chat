package com.pvchat.proximityvoicechat.plugin.socket;

public record SocketMessage(String messageType, Object payload) {
    public static final String DATA = "data";
    public static final String ERROR = "error";
    public static final String WARNING = "warning";
    public static final String INFO = "info";
}
