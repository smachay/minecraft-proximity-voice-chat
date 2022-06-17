package com.pvchat.proximityvoicechat.plugin.socket;

/**
 * Represents error message.
 */
public class ErrorMessage implements SocketMessage {

    private final String messageType = MessageType.ERROR.toString();
    private final String errorMessage;

    public ErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.valueOf(messageType);
    }
}
