package com.pvchat.proximityvoicechat.plugin.distanceMatrix;

import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerDistanceMatrix {

    public ArrayList<Player> players;
    public double distanceMatrix[][];
    ProximityVoiceChat pluginInstance;

    public PlayerDistanceMatrix(ProximityVoiceChat pluginInstance) {
        this.pluginInstance = pluginInstance;
        players = new ArrayList<>();
    }

    public void getPlayers(){
        Collection<? extends Player> p = Bukkit.getOnlinePlayers();
        players = new ArrayList<>(p.stream().toList());
        players.get(0).getWorld().getName();//world
    }

    public void getNewDistanceMatrix() {
        int size = players.size();
        distanceMatrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                distanceMatrix[i][j] = distanceCalculator(players.get(i).getLocation(), players.get(j).getLocation());
                distanceMatrix[j][i] = distanceMatrix[i][j];
            }
        }
    }

    public double distanceCalculator(Location location1, Location location2){
        return Math.sqrt((Math.pow(location1.getX()-location2.getX(),2)+Math.pow(location1.getY()-location2.getY(),2)+Math.pow(location1.getZ()-location2.getZ(),2)));
    }

    public void updateMatrix(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(pluginInstance,  () -> {
            getPlayers();
            if (players.size() > 1) {
                getNewDistanceMatrix();
                //System.out.println(distanceMatrix[0][1] + " " + distanceMatrix[1][0] + " " + distanceMatrix[1][1] + " " + distanceMatrix[0][0]);
            }
        }, 0, 10);
    }
}
