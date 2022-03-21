package com.pvchat.proximityvoicechat.plugin.distanceMatrix;

public class PlayerData {
    private int playerID;
    private String mcNick;
    private String dcNick;
    private String worldName;
    private boolean isOnline;
    private double[] coordinates;

    public PlayerData(int playerID, String mcNick, String dcNick, String worldName, boolean isOnline) {
        this.playerID=playerID;
        this.mcNick = mcNick;
        this.dcNick = dcNick;
        this.worldName = worldName;
        this.isOnline = isOnline;
        this.coordinates = new double[3];
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public String getMcNick() {
        return mcNick;
    }

    public void setMcNick(String mcNick) {
        this.mcNick = mcNick;
    }

    public String getDcNick() {
        return dcNick;
    }

    public void setDcNick(String dcNick) {
        this.dcNick = dcNick;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
}
