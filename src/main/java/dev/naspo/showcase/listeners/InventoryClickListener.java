package dev.naspo.showcase.listeners;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.data.DataManager;
import dev.naspo.showcase.types.PlayerShowcase;
import dev.naspo.showcase.utils.PlayerUtils;
import dev.naspo.showcase.utils.ShowcaseUtils;
import dev.naspo.showcase.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private final Showcase plugin;
    private final DataManager dataManager;

    public InventoryClickListener(Showcase plugin, DataManager dataManager) {
        this.plugin = plugin;
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
        if (!ShowcaseUtils.playerCanEditShowcase(inventoryView, player)) {
            return;
        }

        // Find the showcase object.
        String showcaseOwnerName = ShowcaseUtils.getShowcaseOwnerNameFromInvTitle(inventoryView);
        PlayerShowcase showcase = dataManager.getPlayerShowcases().get(PlayerUtils.findUUIDFromName(showcaseOwnerName));

        // If they are trying to remove an item...
        // (We can determine if they are trying to remove an item by if what they are clicking on is not null,
        // and their mouse isn't holding anything).
        if (event.getCurrentItem() != null && event.getCursor().getType() == Material.AIR) {
            // If the cooldowns feature is enabled and the slot is on cooldown, cancel the event.
            if (Utils.cooldownsFeatureIsEnabled(plugin) && showcase.isSlotOnCooldown(event.getSlot())) {
                event.setCancelled(true);
                return;
            }

            // Process the removal of the item.
            // Remove cooldown lore (if it has lore).
            ItemStack item = event.getCurrentItem();
            ShowcaseUtils.removeCooldownLore(item);
        }
    }
}
