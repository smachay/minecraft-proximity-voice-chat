package com.pvchat.proximityvoicechat.plugin.distanceMatrix;

import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class PlayerDistanceAndVolumeCalculations {

    public ArrayList<Player> players;
    public ArrayList<Player> playersOverwold;
    public ArrayList<Player> playersEnd;
    public ArrayList<Player> playersNether;
    ProximityVoiceChat pluginInstance;
    HashMap<String, ArrayList<Player>> playersToWorld;

    public PlayerDistanceAndVolumeCalculations(ProximityVoiceChat pluginInstance) {
        this.pluginInstance = pluginInstance;
        players = new ArrayList<>();
        playersToWorld = new HashMap<>();
    }

    //Getting Online players
    public void getPlayers() {
        players.clear();
        players.addAll(Bukkit.getOnlinePlayers().stream().toList());
//        playersOverwold = new ArrayList<>();
//        playersNether = new ArrayList<>();
//        playersEnd = new ArrayList<>();

        playersToWorld.clear();

        for(World w : Bukkit.getServer().getWorlds()){
            playersToWorld.put(w.getName(), new ArrayList<>());
        }

        //Dividing per realm
        for (Player temp : players) {
            String world = temp.getWorld().getName();

            playersToWorld.get(world).add(temp);

//            if (world.equals("world")) {
//                playersOverwold.add(temp);
//            } else if (world.equals("world_nether")) {
//                playersNether.add(temp);
//            } else {
//                playersEnd.add(temp);
//            }
        }
    }

    private void addToVolumeList(ArrayList<PlayerVolumeData>  playerVolumeList, ArrayList<Player>  playersOfSomeWorld){
        int size = playersOfSomeWorld.size();
        if (size > 1) {
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    Player p1, p2;
                    p1 = playersOfSomeWorld.get(i);
                    p2 = playersOfSomeWorld.get(j);
                    double distance = distanceCalculator(p1.getLocation(), p2.getLocation());
                    //System.out.println("distance " + distance);
                    long volume = calculateVolume(distance);
                    if (volume != -1) {
                        //PlayerVolumeData temporary = new PlayerVolumeData(p1.getName(), p2.getName(), volume);
                        PlayerVolumeData temporary = new PlayerVolumeData(p1.getUniqueId().toString(),p2.getUniqueId().toString(),volume);
                        playerVolumeList.add(temporary);
                    }
                }
            }
        }
    }

    //getting list of player volume pairs
    public ArrayList<PlayerVolumeData> playerVolumeList() {
        ArrayList<PlayerVolumeData> p = new ArrayList<>();
        if (players.size() > 1) {
            //System.out.println("ilosc graczy na serw: "+players.size());

            for (HashMap.Entry<String,ArrayList<Player>> entry : playersToWorld.entrySet()){
                addToVolumeList(p, entry.getValue());
//                Bukkit.getScheduler().runTaskAsynchronously(pluginInstance, ()->{
//                    addToVolumeList(p, entry.getValue());
//                });
            }




//            addToVolumeList(p, playersOverwold);
//            addToVolumeList(p, playersNether);
//            addToVolumeList(p, playersEnd);

        }

        if (p.size() == 0) {
            //System.out.println("null volume list");
            return null;
        }
        return p;
    }


    //Calculating Distance
    private double distanceCalculator(Location location1, Location location2) {
        return location1.distance(location2);
    }

    //Calculating Volume
    private long calculateVolume(double distance) {
        if (distance > 110) {
            return -1;
        } else if (distance <= 10) {
            return 100;
        } else {
            return Math.round(110 - distance);
        }

    }

    //updating player list
    public void updatePlayerList() {

        //sync

        Bukkit.getScheduler().runTaskTimer(pluginInstance, () -> {
            getPlayers();
            ArrayList<PlayerVolumeData> volumeList=playerVolumeList();
            if (volumeList!=null){
                for (PlayerVolumeData temp : volumeList) {
                    System.out.println("Player1: " + temp.getPlayer1ID() + " Player2: " + temp.getPlayer2ID() + " Volume: " + temp.getVolumeLevel());
                }
            }
        }, 0, 10);

        //async

//        Bukkit.getScheduler().runTaskTimerAsynchronously(pluginInstance, () -> {
//            getPlayers();
//            ArrayList<PlayerVolumeData> volumeList=playerVolumeList();
//            if (volumeList!=null){
//                for (PlayerVolumeData temp : volumeList) {
//                    System.out.println("Player1: " + temp.getPlayer1ID() + " Player2: " + temp.getPlayer2ID() + " Volume: " + temp.getVolumeLevel());
//                }
//            }
//        }, 0, 10);

//        Bukkit.getScheduler().scheduleAsyncRepeatingTask(pluginInstance, () -> {
//            getPlayers();
//            ArrayList<PlayerVolumeData> volumeList=playerVolumeList();
//            if (volumeList!=null){
//                for (PlayerVolumeData temp : volumeList) {
//                    System.out.println("Player1: " + temp.getPlayer1ID() + " Player2: " + temp.getPlayer2ID() + " Volume: " + temp.getVolumeLevel());
//                }
//            }
//        }, 0, 10);
    }
}
