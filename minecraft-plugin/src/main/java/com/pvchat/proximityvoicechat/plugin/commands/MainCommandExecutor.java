package com.pvchat.proximityvoicechat.plugin.commands;

import com.pvchat.proximityvoicechat.plugin.ProximityVoiceChat;
import com.pvchat.proximityvoicechat.plugin.config.ConfigManager;
import com.pvchat.proximityvoicechat.plugin.config.DiscordUserID;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class handles executing Proximity Voice Chat plugin commands.
 */
public class MainCommandExecutor implements CommandExecutor, TabCompleter {
    private ConfigManager config;
    private Logger logger;

    private ProximityVoiceChat plugin;

    /**
     * Creates new instance of {@link MainCommandExecutor}.
     * @param plugin Proximity Voice Chat plugin.
     */
    public MainCommandExecutor(ProximityVoiceChat plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.logger = ProximityVoiceChat.instance.getLogger();
        this.config = plugin.getConfigManager();
    }

    /**
     * Method executed when user uses command.
     * @param sender command sender.
     * @param command command
     * @param alias alias
     * @param args command arguments.
     * @return true if command was executed, false if given command is not supported.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "reload" -> {
                    reloadPlugin(sender);
                    return true;
                }
                case "debug" -> {
                    sendDebugInfo(sender);
                    return true;
                }
                default -> {
                    return false;
                }
            }
        }
        printUsageInfo(sender);
        return true;
    }

    /**
     * Prints available Proximity Voice Chat plugin commands.
     * @param sender command sender.
     */
    private void printUsageInfo(CommandSender sender) {
        String message = ChatColor.GRAY + "Command usage:" +
                "\n    /pvc - print this message" +
                "\n    /pvc reload - reload plugin config" +
                "\n    /pvc debug - print debug info";
        sender.sendMessage(message);
    }

    /**
     * Sends debug info.
     * @param sender command sender.
     */
    private void sendDebugInfo(CommandSender sender) {
        var connectedPlayers = plugin.getSocketServer().getConnectedClientList();
        var message = new StringBuilder();
        message.append(ChatColor.GRAY).append("Opened socket connection list:\n")
                .append(ChatColor.GREEN);
        if (connectedPlayers.size() == 0) {
            message.append("Empty");
        } else {
            connectedPlayers.forEach((s, s2) ->
                    message.append("\n    ")
                            .append(plugin.getDiscordLink().getMinecraftID(DiscordUserID.parse(s)).get())
                            .append("(")
                            .append(s)
                            .append(")")
                            .append(": ")
                            .append(s2)
            );
        }
        sender.sendMessage(message.toString());
    }

    /**
     * Reloads Proximity Voice Chat plugin.
     * @param sender command sender.
     */
    private void reloadPlugin(CommandSender sender) {
        if (!(sender instanceof ConsoleCommandSender)) sender.sendMessage(ChatColor.GRAY + "Starting reload...");
        logger.info("Starting reload...");
        config.reload();
        logger.info("Reload finished.");
        if (!(sender instanceof ConsoleCommandSender)) sender.sendMessage(ChatColor.GRAY + "Reload finished");
    }

    /**
     * Method for displaying command snippets when user presses TAB while using "/pvc" command.
     * @param sender command sender.
     * @param command command.
     * @param alias alias.
     * @param args command arguments.
     * @return list of /pvc command options.
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completeList = null;
        if (args.length == 1) completeList = Arrays.asList("reload", "debug");
        return completeList;
    }
}
