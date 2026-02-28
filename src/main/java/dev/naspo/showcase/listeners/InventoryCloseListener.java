package dev.naspo.showcase.listeners;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.datamanagement.DataManager;
import dev.naspo.showcase.types.PlayerShowcase;
import dev.naspo.showcase.utils.PlayerUtils;
import dev.naspo.showcase.utils.ShowcaseUtils;
import dev.naspo.showcase.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InventoryCloseListener implements Listener {

    private final Showcase plugin;
    private final DataManager dataManager;

    public InventoryCloseListener(Showcase plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    // Updates in-memory showcase data when the inventory closes.
    @EventHandler
    private void onInvClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryView inventoryView = event.getView();

        // If it's not a showcase inventory, ignore.
        if (!ShowcaseUtils.isShowcaseInventory(inventoryView)) {
            return;
        }

        Player player = (Player) event.getPlayer();

        // Permission check.
        if (!ShowcaseUtils.playerCanEditShowcase(inventoryView, player)) {
            return;
        }

        // Capture the showcase object.
        String showcaseOwnerName = ShowcaseUtils.getShowcaseOwnerNameFromInvTitle(inventoryView);
        UUID showcaseOwnerUUID = PlayerUtils.findUUIDFromName(showcaseOwnerName);
        PlayerShowcase showcase = dataManager.getPlayerShowcases().get(showcaseOwnerUUID);

        // Call to process the showcase upon close.
        processShowcaseUponClose(showcase, inventory);
    }

    // This method applies cooldowns to newly added items and save the new showcase contents to the showcase object.
    // Note: Cooldowns will be applied here regardless of whether the cooldowns feature is enabled in the config.
    // Cooldowns feature check and enforcement is only done when opening and removing items.
    private void processShowcaseUponClose(PlayerShowcase showcase, Inventory inventory) {
        ItemStack[] itemsBefore = showcase.getItems();
        ItemStack[] itemsAfter = inventory.getContents();

        // Diff the contents of the inventory from before it was opened, to now after it being closed
        // and apply a cooldown to slots with newly added items.
        for (int i = 0; i < itemsBefore.length; i++) {
            ItemStack itemBefore = itemsBefore[i];
            ItemStack itemAfter = itemsAfter[i];

            if (itemBefore == null || itemBefore.getType().isAir()) {
                if (itemAfter != null && !itemAfter.getType().isAir()) {
                    // Something has been added. Apply cooldown to the slot.
                    long unlockTime = System.currentTimeMillis() + ShowcaseUtils.getCooldownDuration(plugin);
                    showcase.getSlotCooldowns().put(i, unlockTime);
                }
            }
        }

        // Save new showcase content.
        showcase.setItems(itemsAfter);
    }
}
