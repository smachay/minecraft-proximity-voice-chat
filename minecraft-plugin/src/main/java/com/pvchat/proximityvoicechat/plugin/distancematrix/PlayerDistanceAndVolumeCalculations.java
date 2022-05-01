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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerDistanceAndVolumeCalculations {

    private final ProximityVoiceChat pluginInstance;
    private final DiscordLink discordLink;
    private final Consumer<List<PlayerVolumeData>> stateChangeListener;

    private final int maxHearDistance;
    private final int noAttenuationDistance;
    private final double volumeIncreaseFactor;


    public PlayerDistanceAndVolumeCalculations(ProximityVoiceChat pluginInstance, int maxHearDistance, int noAttenuationDistance, Consumer<List<PlayerVolumeData>> stateChangeConsumer) {
        this.noAttenuationDistance = noAttenuationDistance;
        this.maxHearDistance = maxHearDistance;
        this.pluginInstance = pluginInstance;
        stateChangeListener = stateChangeConsumer;
        discordLink = pluginInstance.getDiscordLink();
        double volumeChangeSpectrum = (double) maxHearDistance - (double) noAttenuationDistance;
        volumeIncreaseFactor = (double) 100 / volumeChangeSpectrum;
    }

    //adding payers that hear each other to the volume list
    private void addToVolumeList(List<PlayerVolumeData> playerVolumeList, List<Player> playersOfSomeWorld) {
        int size = playersOfSomeWorld.size();

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                //By using for loop in this way(j=i+1) we can avoid checking
                //volume between two different players more than once which is efficient.

                Player p1 = playersOfSomeWorld.get(i);
                Player p2 = playersOfSomeWorld.get(j);

                Optional<DiscordUserID> optionalP1DiscordID = discordLink.getDiscordID(p1.getUniqueId());
                Optional<DiscordUserID> optionalP2DiscordID = discordLink.getDiscordID(p2.getUniqueId());
                if (optionalP1DiscordID.isEmpty() || optionalP2DiscordID.isEmpty()) continue;
                //Skiping situations where discord id for any of two players that we check is empty

                int volume;
                //Calculating volume between two players on the same world

                //We add players pairs to volume list only if they can hear each other
                playerVolumeList.add(new PlayerVolumeData(optionalP1DiscordID.get(), optionalP2DiscordID.get(), volume));
            }
        }
    }

    //getting list of player volume pairs
    public List<PlayerVolumeData> getPlayerVolumeList() {
        ArrayList<PlayerVolumeData> playerVolumeData = new ArrayList<>();
//        if (Bukkit.getOnlinePlayers().size() > 1) {
        Bukkit.getServer().getWorlds().forEach(world -> addToVolumeList(playerVolumeData, new ArrayList<>(Bukkit.getOnlinePlayers())));
//        }
        return playerVolumeData;
    }

    public static List<PlayerVolumeData> filterPlayerVolumeData(List<PlayerVolumeData> matrix, DiscordUserID discordUserID) {
        return matrix.stream().filter(playerVolumeData -> playerVolumeData.getPlayer1().equals(discordUserID) || playerVolumeData.getPlayer2().equals(discordUserID)).collect(Collectors.toList());
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
            for (PlayerVolumeData pl : getPlayerVolumeList()) {
                System.out.println(pl.getPlayer1() + " " + pl.getPlayer2() + " " + pl.getVolumeLevel());
            }
            stateChangeListener.accept(getPlayerVolumeList());
        }, 0, 10);
    }
}
