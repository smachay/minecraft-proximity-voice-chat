package com.pvchat.proximityvoicechat.plugin.socket;

/**
 * Represents message type of {@link SocketMessage} which is being used to communicate between discord and minecraft plugins. Message types are self-explanatory.
 */
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

/**
 * Stores message which is being used to communicate between discord and minecraft plugins. Message type is represented by {@link MessageType}.
 */
public interface SocketMessage {

    public MessageType getMessageType();

}
