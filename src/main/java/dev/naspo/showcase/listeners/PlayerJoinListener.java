package dev.naspo.showcase.listeners;

import dev.naspo.showcase.datamanagement.DataManager;
import dev.naspo.showcase.types.PlayerShowcase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinListener implements Listener {
    private final DataManager dataManager;

    public PlayerJoinListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    // Creates a showcase for every player when they join, if they don't already have one.
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // If the player does not have a showcase, create one for them.
        if (!dataManager.getPlayerShowcases().containsKey(player.getUniqueId())) {
            PlayerShowcase showcase = new PlayerShowcase(player.getUniqueId());
            dataManager.getPlayerShowcases().put(player.getUniqueId(), showcase);
        }
    }
}
