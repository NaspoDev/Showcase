package dev.naspo.showcase.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class Events implements Listener {
    private DataManager dataManager;

    public Events(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    // Creates a showcase for every player when they join, if they don't already have one.
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // If the player does not have a showcase, create one for them.
        if (!dataManager.playerHasShowcase(player.getUniqueId())) {
            dataManager.putPlayerShowcase(player.getUniqueId(), new ShowcaseItem[0]);
        }
    }

    // Manages edit permissions for a showcase when one is opened.
    @EventHandler
    private void onInvClick(InventoryClickEvent event) {
        String invTitle = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();

        // If it's a showcase inventory.
        if (invTitle.contains("'s Showcase")) {
            // If it's the player's own showcase, but they don't have the basic showcase.use permission,
            // or the moderator showcase.edit permission, cancel the event.
            if (player.getName().equalsIgnoreCase(invTitle.substring(0, invTitle.lastIndexOf("'")))) {
                if (!player.hasPermission("showcase.use") && !player.hasPermission("showcase.edit")) {
                    event.setCancelled(true);
                } else {
                    // Otherwise it's their showcase, and they have permission to edit it.
                    if ()
                }
            // Otherwise, it's not their showcase.
            // But if they don't have the showcase.edit permission, cancel the event.
            } else if (!(event.getWhoClicked().hasPermission("showcase.edit"))) {
                event.setCancelled(true);
            }
        }
    }

    // Saving showcase contents to HashMap on inv close.
    @EventHandler
    private void onInvClose(InventoryCloseEvent event) {
        String invTitle = event.getView().getTitle();
        Player player = (Player) event.getPlayer();

        // If it's a showcase inventory.
        if (invTitle.contains("'s Showcase")) {

            // If the owner of the showcase closed it, save the contents.
            String invOwnerName = invTitle.substring(0, invTitle.lastIndexOf("'"));
            if (player.getName().equalsIgnoreCase(invOwnerName)) {
                dataManager.invs.put(event.getPlayer().getUniqueId().toString(), event.getInventory().getContents());
                return;
            }

            // Or, if someone with showcase edit perms closed it, save the contents.
            if (event.getPlayer().hasPermission("showcase.edit")) {
                String uuid;
                List<Player> players = new ArrayList<>();
                players.addAll(Bukkit.getOnlinePlayers());
                for (Player p : players) {
                    if (invOwnerName.equalsIgnoreCase(p.getName())) {
                        uuid = Bukkit.getPlayer(invOwnerName).getUniqueId().toString();
                        dataManager.invs.put(uuid, event.getInventory().getContents());
                        return;
                    }
                }
                OfflinePlayer p = Bukkit.getOfflinePlayer(invOwnerName);
                uuid = p.getUniqueId().toString();
                dataManager.invs.put(uuid, event.getInventory().getContents());
            }
        }
    }
}
