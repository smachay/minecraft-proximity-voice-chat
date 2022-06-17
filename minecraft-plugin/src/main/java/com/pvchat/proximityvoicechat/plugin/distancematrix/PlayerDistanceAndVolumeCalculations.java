package com.pvchat.proximityvoicechat.plugin.distancematrix;

import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import com.pvchat.proximityvoicechat.plugin.config.DiscordUserID;
import com.pvchat.proximityvoicechat.plugin.config.linkmanagers.DiscordLink;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Class responsible for counting volume level between each player on the server.
 */
public class PlayerDistanceAndVolumeCalculations {

    private final ProximityVoiceChat pluginInstance;
    private final DiscordLink discordLink;
    private final Consumer<List<PlayerVolumeData>> stateChangeListener;

    private final int maxHearDistance;
    private final int noAttenuationDistance;
    private final double volumeIncreaseFactor;

    /**
     * Creates new {@link PlayerDistanceAndVolumeCalculations} instance.
     *
     * @param pluginInstance        plugin instance.
     * @param maxHearDistance       max distance (minecraft blocks count) in which two players can hear each other.
     * @param noAttenuationDistance distance (minecraft blocks count) in which two players can hear each other without decreasing volume level.
     * @param stateChangeConsumer method for sending volume data, it is implemented in {@link com.pvchat.proximityvoicechat.plugin.socket.PlayerVolumeServer}
     */
    public PlayerDistanceAndVolumeCalculations(ProximityVoiceChat pluginInstance, int maxHearDistance, int noAttenuationDistance, Consumer<List<PlayerVolumeData>> stateChangeConsumer) {
        this.noAttenuationDistance = noAttenuationDistance;
        this.maxHearDistance = maxHearDistance;
        this.pluginInstance = pluginInstance;
        stateChangeListener = stateChangeConsumer;
        discordLink = pluginInstance.getDiscordLink();
        double volumeChangeSpectrum = (double) maxHearDistance - (double) noAttenuationDistance;
        volumeIncreaseFactor = (double) 100 / volumeChangeSpectrum;
    }

    /**
     * Adds players that hear each other to the volume list
     */

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
                if (p1.getWorld() != p2.getWorld()) volume = 0;
                else volume = calculateVolume(distanceBetweenTwoPlayers(p1.getLocation(), p2.getLocation()));

                //We add players pairs to volume list only if they can hear each other
                playerVolumeList.add(new PlayerVolumeData(optionalP1DiscordID.get(), optionalP2DiscordID.get(), volume));
            }
        }
    }

    /**
     * Returns volume level list - list of {@link PlayerVolumeData} storing volume level between players.
     * @return volume level list - list of {@link PlayerVolumeData} storing volume level between players.
     */
    public List<PlayerVolumeData> getPlayerVolumeList() {
        ArrayList<PlayerVolumeData> playerVolumeData = new ArrayList<>();
//        if (Bukkit.getOnlinePlayers().size() > 1) {
        Bukkit.getServer().getWorlds().forEach(world -> addToVolumeList(playerVolumeData, new ArrayList<>(Bukkit.getOnlinePlayers())));
//        }
        return playerVolumeData;
    }

    /**
     * Filters volume level list so as to extract {@link PlayerVolumeData} related to user with given discordUserID.
     * @param matrix volume list
     * @param discordUserID user discordID
     * @return volume level list with the only volumes related to user with given discordUserID.
     */
    public static List<PlayerVolumeData> filterPlayerVolumeData(List<PlayerVolumeData> matrix, DiscordUserID discordUserID) {
        return matrix.stream().filter(playerVolumeData -> playerVolumeData.getPlayer1().equals(discordUserID) || playerVolumeData.getPlayer2().equals(discordUserID)).collect(Collectors.toList());
    }


    /**
     * Calculates distance between two locations
     * @param location1 location1
     * @param location2 location2
     * @return distance between locations.
     */
    private double distanceBetweenTwoPlayers(Location location1, Location location2) {
        return location1.distance(location2);
    }

    /**
     * Calculates volume level basing on distance and plugin configuration.
     * @param distance distance
     * @return volume level basing on given distance.
     */
    private int calculateVolume(double distance) {
        if (distance > maxHearDistance) {
            return 0;
        } else if (distance <= noAttenuationDistance) {
            return 100;
        } else {
            return (int) Math.round(100 - ((distance - noAttenuationDistance) * volumeIncreaseFactor));
        }
    }

    /**
     * Sends player volume levels list to every connected player.
     * @param plugin
     */
    public void updateVolume(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (PlayerVolumeData pl : getPlayerVolumeList()) {
                System.out.println(pl.getPlayer1() + " " + pl.getPlayer2() + " " + pl.getVolumeLevel());
            }
            stateChangeListener.accept(getPlayerVolumeList());
        }, 0, 10);
    }
}
