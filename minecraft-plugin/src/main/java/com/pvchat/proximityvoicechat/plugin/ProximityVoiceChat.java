package com.pvchat.proximityvoicechat.plugin;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.pvchat.proximityvoicechat.commands.ExampleCommand;
import com.pvchat.proximityvoicechat.listeners.ExampleListener;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProximityVoiceChat extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("[PLUGIN] Proximity Voice Chat plugin loaded.");
        getServer().getPluginManager().registerEvents(new ExampleListener(), this);
        getCommand("moveup").setExecutor(new ExampleCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("[PLUGIN] Disabling Proximity Voice Chat plugin.");
    }
}
