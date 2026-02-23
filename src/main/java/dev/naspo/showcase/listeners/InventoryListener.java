package dev.naspo.showcase.listeners;

import dev.naspo.showcase.datamanagement.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Event listener for inventory related events.
public class InventoryListener implements Listener {

    // A showcase inventory will always end with this text.
    private final String SHOWCASE_INVENTORY_TITLE_ENDING = "'s Showcase";
    private final DataManager dataManager;

    public InventoryListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    // Manages edit permissions for a showcase when one is opened.
    @EventHandler
    private void onInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(event.getView().getBottomInventory())) {
            event.getWhoClicked().sendMessage("You didn't interact with the showcase gui. Ignoring");
            return;
        }
        String invTitle = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();

        // If it's a showcase inventory.
        if (invTitle.contains(SHOWCASE_INVENTORY_TITLE_ENDING)) {
            // If it's the player's own showcase...
            if (player.getName().equalsIgnoreCase(invTitle.substring(0, invTitle.lastIndexOf("'")))) {
                // If they are trying to remove an item...
                if (event.getCurrentItem() != null && event.getCursor().getType() == Material.AIR) {
                    player.sendMessage("You are trying to remove an item");

                    // If the slot is on cooldown...
                    if (dataManager.getPlayerShowcaseSlotCooldowns().get(player.getUniqueId().toString()).get(event.getSlot()) >
                            System.currentTimeMillis()) {
                        player.sendMessage("This item is currently on cooldown.");
                        event.setCancelled(true);
                        return;
                    }
                    player.sendMessage("This item is not on cooldown.");

                    // Remove cooldown lore (if it has lore)
                    ItemStack item = event.getCurrentItem();
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = meta.getLore();
                    if (lore != null) {
                        for  (int i = 0; i < lore.size(); i++) {
                            if (lore.get(i).startsWith("Cooldown time remaining: ")) {
                                lore.remove(i);
                            }
                        }
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }


                    if (!player.hasPermission("showcase.use") && !player.hasPermission("showcase.edit")) {
                        event.setCancelled(true);
                    }
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

        // If it's a showcase inventory.
        if (invTitle.contains(SHOWCASE_INVENTORY_TITLE_ENDING)) {

            // If the owner of the showcase closed it, save the contents.
            String invOwnerName = invTitle.substring(0, invTitle.lastIndexOf("'"));
            if (event.getPlayer().getName().equalsIgnoreCase(invOwnerName)) {

                // NEW COOLDOWN STUFF
                // diff the contents and apply a cooldown to slots with newly added items
                ItemStack[] itemsBefore = dataManager.getPlayerShowcases().get(event.getPlayer().getUniqueId().toString());
                ItemStack[] itemsAfter = event.getInventory().getContents();

                for (int i = 0; i < itemsBefore.length; i++) {
                    ItemStack itemBefore = itemsBefore[i];
                    ItemStack itemAfter = itemsAfter[i];

                    if (itemBefore == null || itemBefore.getType().isAir()) {
                        if (itemAfter != null && !itemAfter.getType().isAir()) {
                            // Something has been added. Apply cooldown to the slot.
                            long unlockTime = System.currentTimeMillis() + 10000L;
                            dataManager.getPlayerShowcaseSlotCooldowns().computeIfAbsent(
                                            event.getPlayer().getUniqueId().toString(), k -> new HashMap<>())
                                    .put(i, unlockTime);
                            // Add lore to the item
                            ItemMeta meta = itemAfter.getItemMeta();
                            List<String> lore = meta.getLore();
                            if (lore == null) {
                                lore = new ArrayList<>();
                            }
                            lore.add("Cooldown time remaining: " + (unlockTime - System.currentTimeMillis()));
                            meta.setLore(lore);
                            itemAfter.setItemMeta(meta);
                            itemsAfter[i] = itemAfter;
                        }
                    }
                }

                // Save new showcase content.
                dataManager.getPlayerShowcases().put(event.getPlayer().getUniqueId().toString(), itemsAfter);
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
                        dataManager.getPlayerShowcases().put(uuid, event.getInventory().getContents());
                        return;
                    }
                }
                OfflinePlayer p = Bukkit.getOfflinePlayer(invOwnerName);
                uuid = p.getUniqueId().toString();
                dataManager.getPlayerShowcases().put(uuid, event.getInventory().getContents());
            }
        }
    }
}
