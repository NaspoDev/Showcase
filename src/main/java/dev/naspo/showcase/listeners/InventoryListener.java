package dev.naspo.showcase.listeners;

import dev.naspo.showcase.datamanagement.DataManager;
import dev.naspo.showcase.types.PlayerShowcase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Event listener for inventory related events.
public class InventoryListener implements Listener {

    private final DataManager dataManager;

    // A showcase inventory will always end with this text.
    private final String SHOWCASE_INVENTORY_TITLE_ENDING = "'s Showcase";
    private final String COOLDOWN_LORE_PREFIX = "Cooldown: ";

    public InventoryListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    // Manages edit permissions for a showcase when one is opened.
    @EventHandler
    private void onInvClick(InventoryClickEvent event) {
        InventoryView inventoryView = event.getView();

        // If the interaction wasn't with the top inventory. Ignore.
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(inventoryView.getBottomInventory())) {
            return;
        }

        // If it's not a showcase inventory, ignore.
        if (!isShowcaseInventory(inventoryView)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        // If it's not the player's own showcase, and they don't have the edit permission, cancel the event.
        if (!showcaseBelongsTo(inventoryView, player) && !player.hasPermission("showcase.edit")) {
            event.setCancelled(true);
            return;
        }

        // Otherwise, it's the player's own showcase...

        // If they don't have permission to interact with it, cancel the event.
        if (!player.hasPermission("showcase.use") && !player.hasPermission("showcase.edit")) {
            event.setCancelled(true);
            return;
        }

        PlayerShowcase showcase = dataManager.getPlayerShowcases().get(player.getUniqueId());

        // If they are trying to remove an item...
        // (We can determine if they are trying to remove an item if what they are clicking on is not null,
        // and their mouse isn't holding anything).
        if (event.getCurrentItem() != null && event.getCursor().getType() == Material.AIR) {
            // If the slot is on cooldown, cancel the event.
            if (showcase.isSlotOnCooldown(event.getSlot())) {
                event.setCancelled(true);
                return;
            }

            // Otherwise the slot is not on cooldown, so process the removal of the item.
            // Remove cooldown lore (if it has lore).
            ItemStack item = event.getCurrentItem();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();

            if (lore != null) {
                for (int i = 0; i < lore.size(); i++) {
                    if (lore.get(i).startsWith(COOLDOWN_LORE_PREFIX)) {
                        lore.remove(i);
                    }
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
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

    // === Helper Methods ===

    // Returns true if the specified inventory is a showcase inventory.
    private boolean isShowcaseInventory(InventoryView inventoryView) {
        if (inventoryView.getTitle().contains(SHOWCASE_INVENTORY_TITLE_ENDING))
    }

    // Returns true if the specified showcase inventory belongs to the specified player.
    private boolean showcaseBelongsTo(InventoryView inventoryView, Player player) {
        String inventoryTitle = inventoryView.getTitle();
        String playerName = player.getName();
        return playerName.equalsIgnoreCase(inventoryTitle.substring(0, inventoryTitle.lastIndexOf("'")));
    }
}
