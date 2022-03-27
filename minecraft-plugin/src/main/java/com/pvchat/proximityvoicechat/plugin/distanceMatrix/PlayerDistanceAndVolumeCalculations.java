package com.pvchat.proximityvoicechat.plugin.distanceMatrix;

import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerDistanceAndVolumeCalculations {

    public ArrayList<Player> players;
    public ArrayList<Player> playersOverwold;
    public ArrayList<Player> playersEnd;
    public ArrayList<Player> playersNether;
    ProximityVoiceChat pluginInstance;

    public PlayerDistanceAndVolumeCalculations(ProximityVoiceChat pluginInstance) {
        this.pluginInstance = pluginInstance;
        players = new ArrayList<>();
    }

    //Getting Online players
    public void getPlayers() {
        Collection<? extends Player> p = Bukkit.getOnlinePlayers();
        players = new ArrayList<>(p.stream().toList());
        playersOverwold = new ArrayList<>();
        playersNether = new ArrayList<>();
        playersEnd = new ArrayList<>();

        //Dividing per realm
        for (int i = 0; i < players.size(); i++) {
            Player temp = players.get(i);
            String world = temp.getWorld().getName();
            if (world.equals("Overworld")) {
                playersOverwold.add(temp);
            } else if (world.equals("Nether")) {
                playersNether.add(temp);
            } else {
                playersEnd.add(temp);
            }
        }

    }

    //getting list of player volume pairs
    public ArrayList<PlayerVolumeData> playerVolumeList() {
        ArrayList<PlayerVolumeData> p = p = new ArrayList<>();
        if (players.size() > 1) {

            int ov = playersOverwold.size();
            int ne = playersNether.size();
            int en = playersEnd.size();
            if (ov > 1) {
                for (int i = 0; i < ov; i++) {
                    for (int j = i + 1; j < ov; j++) {
                        Player p1, p2;
                        p1 = playersOverwold.get(i);
                        p2 = playersOverwold.get(j);
                        double distance = distanceCalculator(p1.getLocation(), p2.getLocation());
                        int volume = calculateVolume(distance);
                        if (volume != -1) {
                            PlayerVolumeData temporary = new PlayerVolumeData(p1.getName(), p2.getName(), volume);
                            //PlayerVolumeData temporary=new PlayerVolumeData(p1.getUniqueId().toString(),p2.getUniqueId().toString(),volume);
                            p.add(temporary);
                        }
                    }
                }


            }

            if (ne > 1) {
                for (int i = 0; i < ne; i++) {
                    for (int j = i + 1; j < ne; j++) {
                        Player p1, p2;
                        p1 = playersNether.get(i);
                        p2 = playersNether.get(j);
                        double distance = distanceCalculator(p1.getLocation(), p2.getLocation());
                        int volume = calculateVolume(distance);
                        if (volume != -1) {
                            PlayerVolumeData temporary = new PlayerVolumeData(p1.getName(), p2.getName(), volume);
                            //PlayerVolumeData temporary=new PlayerVolumeData(p1.getUniqueId().toString(),p2.getUniqueId().toString(),volume);
                            p.add(temporary);
                        }
                    }
                }
            }

            if (en > 1) {
                for (int i = 0; i < en; i++) {
                    for (int j = i + 1; j < en; j++) {
                        Player p1, p2;
                        p1 = playersEnd.get(i);
                        p2 = playersEnd.get(j);
                        double distance = distanceCalculator(p1.getLocation(), p2.getLocation());
                        int volume = calculateVolume(distance);
                        if (volume != -1) {
                            PlayerVolumeData temporary = new PlayerVolumeData(p1.getName(), p2.getName(), volume);
                            //PlayerVolumeData temporary=new PlayerVolumeData(p1.getUniqueId().toString(),p2.getUniqueId().toString(),volume);
                            p.add(temporary);
                        }
                    }
                }
            }

        }

        if (p.size() == 0) {
            return null;
        }
        return p;
    }


    //Calculating Distance
    private double distanceCalculator(Location location1, Location location2) {
        return location1.distance(location2);
    }

    //Calculating Volume
    private int calculateVolume(double distance) {
        if (distance > 110) {
            return -1;
        } else if (distance <= 10) {
            return 100;
        } else {
            return (int) (110 - distance);
        }

    }

    //updating player list
    public void updatePlayerList() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(pluginInstance, () -> {
            getPlayers();

        }, 0, 10);
    }
}
