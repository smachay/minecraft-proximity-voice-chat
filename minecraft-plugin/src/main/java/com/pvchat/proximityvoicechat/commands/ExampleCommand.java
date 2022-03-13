package com.pvchat.proximityvoicechat.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExampleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("moveup")) {
            if (sender instanceof Player player) {
                try {
                    double height = Double.parseDouble(args[0]);
                    height = (height > 0 ? (height < 255 ? height : 255) : 5);
                    player.sendMessage(ChatColor.AQUA + "You jumped " + height + " blocks up.");
                    Location location = player.getLocation();
                    location.setY(location.getY() + height);
                    player.teleport(location);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }
}
