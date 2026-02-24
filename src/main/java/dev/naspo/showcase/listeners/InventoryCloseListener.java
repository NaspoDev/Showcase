package dev.naspo.showcase.listeners;

import dev.naspo.showcase.datamanagement.DataManager;
import dev.naspo.showcase.types.PlayerShowcase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InventoryCloseListener {

    private final DataManager dataManager;

    public InventoryCloseListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    // Saving showcase contents to memory on inv close.
    @EventHandler
    private void onInvClose(InventoryCloseEvent event) {
        InventoryView inventoryView = event.getView();

        // If it's not a showcase inventory, ignore.
        if (!isShowcaseInventory(inventoryView)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        UUID showcaseOwnerUUID = null;

        // If it's the player's own showcase and they are allowed to edit it, capture the UUID and proceed.
        if (showcaseBelongsTo(inventoryView, player) && player.hasPermission("showcase.use")) {
            showcaseOwnerUUID = player.getUniqueId();
            // If it's someone with edit perms, find the owner, capture their UUID, and proceed.
        } else if (player.hasPermission("showcase.edit")) {
            String showcaseOwnerName = inventoryView.getTitle().substring(0, inventoryView.getTitle().lastIndexOf("'"));
            // Find the showcase owner's uuid.
            // First check online players...
            List<Player> onlinePlayers = new ArrayList<>();
            onlinePlayers.addAll(Bukkit.getOnlinePlayers());
            for (Player p : onlinePlayers) {
                if (p.getName().equalsIgnoreCase(showcaseOwnerName)) {
                    showcaseOwnerUUID = p.getUniqueId();
                }
            }

            // If we still haven't found the showcase owner (i.e. the showcase owner wasn't online),
            // find them as an OfflinePlayer.
            if (showcaseOwnerUUID == null) {
                OfflinePlayer p = Bukkit.getOfflinePlayer(showcaseOwnerName);
                showcaseOwnerUUID = p.getUniqueId();
            }
        } else {
            // Otherwise the person who closed the showcase does not have permission to be editing it. Exit.
            return;
        }

        // Processing a potentially edited showcase upon close...
        // First diff the contents of the inventory from before it was opened, to now after it being closed
        // and apply a cooldown to slots with newly added items.
        PlayerShowcase showcase = dataManager.getPlayerShowcases().get(showcaseOwnerUUID);
        ItemStack[] itemsBefore = showcase.getItems();
        ItemStack[] itemsAfter = event.getInventory().getContents();

        for (int i = 0; i < itemsBefore.length; i++) {
            ItemStack itemBefore = itemsBefore[i];
            ItemStack itemAfter = itemsAfter[i];

            if (itemBefore == null || itemBefore.getType().isAir()) {
                if (itemAfter != null && !itemAfter.getType().isAir()) {
                    // Something has been added. Apply cooldown to the slot.
                    long unlockTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5);
                    showcase.getSlotCooldowns().put(i, unlockTime);

                    // Add lore to the item
                    // TODO: Move this logic to when the showcase is opened.
//                    ItemMeta meta = itemAfter.getItemMeta();
//                    List<String> lore = meta.getLore();
//                    if (lore == null) {
//                        lore = new ArrayList<>();
//                    }
//                    lore.add("Cooldown time remaining: " + (unlockTime - System.currentTimeMillis()));
//                    meta.setLore(lore);
//                    itemAfter.setItemMeta(meta);
//                    itemsAfter[i] = itemAfter;
                }
            }
        }

        // Save new showcase content.
        showcase.setItems(itemsAfter);
    }
}
