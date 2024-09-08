package dev.naspo.showcase.data;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.models.PlayerShowcase;
import dev.naspo.showcase.models.ShowcaseItem;
import dev.naspo.showcase.support.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Events implements Listener {
    private final Showcase plugin;
    private DataManager dataManager;

    public Events(Showcase plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    // Creates a showcase for every player when they join, if they don't already have one.
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // If the player does not have a showcase, create one for them.
        if (!dataManager.playerHasShowcase(player.getUniqueId())) {
            dataManager.putPlayerShowcase(player.getUniqueId(), new PlayerShowcase(plugin));
        }
    }

    // Manages edit permissions for a showcase when one is opened.
    @EventHandler
    private void onInvClick(InventoryClickEvent event) {
        String invTitle = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        PlayerShowcase playerShowcase = dataManager.getPlayerShowcase(player.getUniqueId());

        // If it's a showcase inventory.
        if (invTitle.contains("'s Showcase")) {
            // If they didn't click an actual item, exit.
            if (clickedItem == null) {
                return;
            }

            // If it's the player's own showcase.
            if (player.getName().equalsIgnoreCase(invTitle.substring(0, invTitle.lastIndexOf("'")))) {
                // If they are able to edit it.
                if (player.hasPermission("showcase.use")) {
                    // If a cooldown is active on the item they clicked, cancel the event.
                    if (ShowcaseItem.cooldownIsActive(clickedItem)) {
                        // Unless they have showcase.edit permission which bypasses cooldowns,
                        // then allow the removal and properly remove it.
                        if (player.hasPermission("showcase.edit")) {
                            playerShowcase.removeShowcaseItem(clickedItem);
                        } else {
                            event.setCancelled(true);
                            // Also send a message to the player with a cooldown message.
                            player.sendMessage(Utils.chatColor(Utils.getPluginPrefix() +
                                    plugin.getConfig().getString("messages.cooldown-active")));
                            return;
                        }
                    }
                    // Then a cooldown is not active, allow the removal and properly remove it.
                    playerShowcase.removeShowcaseItem(clickedItem);
                    return;
                }
            }
            // If the player doesn't have the showcase.edit permission, cancel the event.
            if (!(player.hasPermission("showcase.edit"))) {
                event.setCancelled(true);
            } else {
                // If they do, allow the removal and properly remove it.
                playerShowcase.removeShowcaseItem(clickedItem);
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
