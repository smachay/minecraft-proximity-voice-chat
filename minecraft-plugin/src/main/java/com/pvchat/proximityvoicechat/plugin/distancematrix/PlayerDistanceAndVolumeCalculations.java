package com.pvchat.proximityvoicechat.plugin.distancematrix;

import com.pvchat.proximityvoicechat.plugin.config.DiscordUserID;
import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.DiscordLink;
import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class PlayerDistanceAndVolumeCalculations {

    private ProximityVoiceChat pluginInstance;
    private final DiscordLink discordLink;
    private ArrayList<Consumer<List<PlayerVolumeData>>> stateChangeListeners;

    private final int maxHearDistance;
    private final int noAttenuationDistance;
    private final double volumeIncreaseFactor;


    public PlayerDistanceAndVolumeCalculations(ProximityVoiceChat pluginInstance, int maxHearDistance, int noAttenuationDistance) {
        this.noAttenuationDistance = noAttenuationDistance;
        this.maxHearDistance = maxHearDistance;
        this.pluginInstance = pluginInstance;
        stateChangeListeners = new ArrayList<>();
        discordLink = pluginInstance.getDiscordLink();
        double volumeChangeSpectrum = (double) maxHearDistance - (double) noAttenuationDistance;
        volumeIncreaseFactor = (double) 100 / volumeChangeSpectrum;
    }

    public void addStateChangeListener(Consumer<List<PlayerVolumeData>> stateChangeListener) {
        stateChangeListeners.add(stateChangeListener);
    }

    public void removeStateChangeListener(Consumer<List<PlayerVolumeData>> stateChangeListener) {
        stateChangeListeners.remove(stateChangeListener);
    }

    private void addToVolumeList(List<PlayerVolumeData> playerVolumeList, List<Player> playersOfSomeWorld) {
        int size = playersOfSomeWorld.size();
        if (size > 1) {
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    Player p1 = playersOfSomeWorld.get(i);
                    Player p2 = playersOfSomeWorld.get(j);

                    Optional<DiscordUserID> optionalP1DiscordID = discordLink.getDiscordID(p1.getUniqueId());
                    Optional<DiscordUserID> optionalP2DiscordID = discordLink.getDiscordID(p2.getUniqueId());
                    if (optionalP1DiscordID.isEmpty() || optionalP2DiscordID.isEmpty()) continue;

                    double distance = distanceBetweenTwoPlayers(p1.getLocation(), p2.getLocation());
                    int volume = calculateVolume(distance);
                    if (volume > 0) {
                        playerVolumeList.add(new PlayerVolumeData(optionalP1DiscordID.get(), optionalP2DiscordID.get(), volume));
                    }
                }
            }
        }
    }

    //other version, also not complete
    private void addToVolumeList2(List<PlayerVolumeData> playerVolumeList, List<Player> playersOfSomeWorld) {
        Stream<Player> pl = playersOfSomeWorld.stream();
        playersOfSomeWorld.forEach(p1 -> pl.filter(p2 -> p2 != p1).forEach(p2 -> {

            Optional<DiscordUserID> optionalP1DiscordID = discordLink.getDiscordID(p1.getUniqueId());
            Optional<DiscordUserID> optionalP2DiscordID = discordLink.getDiscordID(p2.getUniqueId());

            if (optionalP1DiscordID.isPresent() && optionalP2DiscordID.isPresent()) {
                double distance = distanceBetweenTwoPlayers(p1.getLocation(), p2.getLocation());
                int volume = calculateVolume(distance);
                if (volume > 0) {
                    PlayerVolumeData temporary = new PlayerVolumeData(optionalP1DiscordID.get(), optionalP2DiscordID.get(), volume);
                    playerVolumeList.add(temporary);
                }
            }

        }));
    }

    //getting list of player volume pairs
    public List<PlayerVolumeData> getPlayerVolumeList() {
        ArrayList<PlayerVolumeData> playerVolumeData = new ArrayList<>();
        if (Bukkit.getOnlinePlayers().size() > 1) {
            Bukkit.getServer().getWorlds().forEach(world -> addToVolumeList(playerVolumeData, world.getPlayers()));
        }
        return playerVolumeData;
    }


    //Calculating Distance
    private double distanceBetweenTwoPlayers(Location location1, Location location2) {
        return location1.distance(location2);
    }

    //Calculating Volume
    private int calculateVolume(double distance) {
        if (distance > maxHearDistance) {
            return 0;
        } else if (distance <= noAttenuationDistance) {
            return 100;
        } else {
            return (int) Math.round(100 - ((distance - noAttenuationDistance) * volumeIncreaseFactor));
        }
    }

    //updating player list
    public void updateVolume(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            stateChangeListeners.stream().forEach((Consumer<List<PlayerVolumeData>> hashMapConsumer) -> {
                hashMapConsumer.accept(getPlayerVolumeList());
            });
        }, 0, 10);
    }
}
