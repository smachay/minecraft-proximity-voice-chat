package com.pvchat.proximityvoicechat;

import org.bukkit.plugin.java.JavaPlugin;

public final class ProximityVoiceChat extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("[PLUGIN] Proximity Voice Chat plugin loaded.");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("[PLUGIN] Disabling Proximity Voice Chat plugin.");
    }
}
