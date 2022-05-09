package com.pvchat.proximityvoicechat.plugin.socket;

public class DataMessage implements SocketMessage {

    private final String messageType = MessageType.DATA.toString();
    private final VolumeData[] volumeData;

    public DataMessage(VolumeData[] volumeData) {
        this.volumeData = volumeData;
    }

    public VolumeData[] getVolumeData() {
        return volumeData;
    }


    @Override
    public MessageType getMessageType() {
        return MessageType.valueOf(messageType);
    }
}
