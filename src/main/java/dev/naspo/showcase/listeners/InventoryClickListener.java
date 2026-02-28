package dev.naspo.showcase.listeners;

import dev.naspo.showcase.datamanagement.DataManager;
import dev.naspo.showcase.types.PlayerShowcase;
import dev.naspo.showcase.utils.PlayerUtils;
import dev.naspo.showcase.utils.ShowcaseUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class InventoryClickListener implements Listener {

    private final DataManager dataManager;

    public InventoryClickListener(DataManager dataManager) {
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
        if (!ShowcaseUtils.isShowcaseInventory(inventoryView)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        // Permission check.
        if (!playerCanEditShowcase(inventoryView, player)) {
            return;
        }

        // Find the showcase object.
        String showcaseOwnerName = ShowcaseUtils.getShowcaseOwnerNameFromInvTitle(inventoryView);
        PlayerShowcase showcase;

        if (PlayerUtils.isOnline(showcaseOwnerName)) {
            Player showcaseOwner = PlayerUtils.getOnlinePlayer(showcaseOwnerName);
            showcase = dataManager.getPlayerShowcases().get(showcaseOwner.getUniqueId());
        } else {
            OfflinePlayer showcaseOwner = PlayerUtils.getOfflinePlayer(showcaseOwnerName);
            showcase = dataManager.getPlayerShowcases().get(showcaseOwner.getUniqueId());
        }

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
                ShowcaseUtils.removeCooldownLore(lore);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
    }

    // Returns true if the player has permission to edit the showcase.
    // Checks via inventory title.
    private boolean playerCanEditShowcase(InventoryView inventoryView, Player player) {
        // If they have the edit permission, or it's their showcase and they have the "use" permission, return true.
        return player.hasPermission("showcase.edit")
                || (ShowcaseUtils.showcaseBelongsTo(inventoryView, player) && player.hasPermission("showcase.use"));
    }
}
