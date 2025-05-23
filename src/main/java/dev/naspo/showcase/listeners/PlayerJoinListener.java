package dev.naspo.showcase.listeners;

import dev.naspo.showcase.datamanagement.Data;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinListener implements Listener {

    // Creates a showcase for every player when they join, if they don't already have one.
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // If the player does not have a showcase, create one for them.
        if (!Data.invs.containsKey(player.getUniqueId().toString())) {
            Data.invs.put(player.getUniqueId().toString(), new ItemStack[0]);
        }
    }
}
