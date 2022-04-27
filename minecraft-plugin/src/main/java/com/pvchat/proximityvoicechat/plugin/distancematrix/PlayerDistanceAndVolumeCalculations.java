package com.pvchat.proximityvoicechat.plugin.distancematrix;

import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.DiscordLink;
import com.pvchat.proximityvoicechat.plugin.config.DiscordUserID;
import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Consumer;

public class PlayerDistanceAndVolumeCalculations {

    //public List<Player> players;
    private ProximityVoiceChat pluginInstance;
    //private HashMap<String, List<Player>> playersToWorld;
    private final DiscordLink discordLink;

    private ArrayList<Consumer<List<PlayerVolumeData>>> stateChangeListeners;

    public PlayerDistanceAndVolumeCalculations(ProximityVoiceChat pluginInstance){//(ProximityVoiceChat pluginInstance) {
        this.pluginInstance = pluginInstance;
        //players = new ArrayList<>();
        //playersToWorld = new HashMap<>();
        stateChangeListeners = new ArrayList<>();
        discordLink = pluginInstance.getDiscordLink();
    }

    public void addStateChangeListener(Consumer<List<PlayerVolumeData>> stateChangeListener){
        stateChangeListeners.add(stateChangeListener);
    }

    public void removeStateChangeListener(Consumer<List<PlayerVolumeData>> stateChangeListener) {
        stateChangeListeners.remove(stateChangeListener);
    }

    //Getting Online players
    public HashMap<String, List<Player>> getPlayers() {
//        players.clear();
//        players.addAll(Bukkit.getOnlinePlayers().stream().toList());
//
//        playersToWorld.clear();

        HashMap<String, List<Player>> playersToWorld = new HashMap<>();

        for(World w : Bukkit.getServer().getWorlds()){
            playersToWorld.put(w.getName(), new ArrayList<>());
        }

        //Dividing per realm
        for (Player temp : Bukkit.getOnlinePlayers()) {
            String world = temp.getWorld().getName();

            playersToWorld.get(world).add(temp);
        }
        return playersToWorld;
    }

    private void addToVolumeList(List<PlayerVolumeData>  playerVolumeList, List<Player>  playersOfSomeWorld){
        int size = playersOfSomeWorld.size();
        if (size > 1) {
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    Player p1 = playersOfSomeWorld.get(i);
                    Player p2 = playersOfSomeWorld.get(j);

                    Optional<DiscordUserID> optionalP1DiscordID = discordLink.getDiscordID(p1.getUniqueId());
                    Optional<DiscordUserID> optionalP2DiscordID = discordLink.getDiscordID(p2.getUniqueId());
                    if(optionalP1DiscordID.isEmpty() || optionalP2DiscordID.isEmpty()) continue;

                    double distance = distanceCalculator(p1.getLocation(), p2.getLocation());
                    int volume = calculateVolume(distance);
                    if (volume != -1) {
                        PlayerVolumeData temporary = new PlayerVolumeData(optionalP1DiscordID.get(), optionalP2DiscordID.get(), volume);
                        playerVolumeList.add(temporary);
                    }
                }
            }
        }
    }

    //getting list of player volume pairs
    public List<PlayerVolumeData> playerVolumeList() {
        ArrayList<PlayerVolumeData> p = new ArrayList<>();
        if (Bukkit.getOnlinePlayers().size() > 1) {
            for (Map.Entry<String,List<Player>> entry : getPlayers().entrySet()){
                addToVolumeList(p, entry.getValue());
            }

        }
        if (p.isEmpty()) {
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
            return (int) Math.round(110 - distance);
        }

    }

    //updating player list
    public void updatePlayerList(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            //getPlayers();
            var playerVolumeMatrix = playerVolumeList();
            stateChangeListeners.stream().forEach(hashMapConsumer -> hashMapConsumer.accept(playerVolumeMatrix));
//            ArrayList<PlayerVolumeData> volumeList = playerVolumeList();
//            if (volumeList!=null){
//                for (PlayerVolumeData temp : volumeList) {
//                    System.out.println("Player1: " + temp.getPlayer1ID() + " Player2: " + temp.getPlayer2ID() + " Volume: " + temp.getVolumeLevel());
//                }
//            }
        }, 0, 10);
    }
}
