package com.pvchat.proximityvoicechat.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class ExampleListener implements Listener {
    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event){
        System.out.println(event.getPlayer().getPlayerProfile().getName() + " jumped.");
    }
}
