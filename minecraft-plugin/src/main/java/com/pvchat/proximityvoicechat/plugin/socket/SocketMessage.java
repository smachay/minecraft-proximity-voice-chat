package com.pvchat.proximityvoicechat.plugin.socket;

enum MessageType {
    DATA("data"),
    ERROR("error"),
    WARNING("warning"),
    INFO("info");

    private final String messageType;

    @Override
    public String toString() {
        return messageType;
    }

    MessageType(String messageType) {
        this.messageType = messageType;
    }
}

public interface SocketMessage {

    public MessageType getMessageType();

}
