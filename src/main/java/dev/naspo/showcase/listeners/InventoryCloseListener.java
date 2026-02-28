package dev.naspo.showcase.listeners;

import dev.naspo.showcase.datamanagement.DataManager;
import dev.naspo.showcase.types.PlayerShowcase;
import dev.naspo.showcase.utils.PlayerUtils;
import dev.naspo.showcase.utils.ShowcaseUtils;
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

    private final DataManager dataManager;

    public InventoryCloseListener(DataManager dataManager) {
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

        // The showcase could have been edited. We need to process it.
        processShowcaseUponClose(showcase, inventory);
    }

    // The showcase could have been edited upon close. This method diffs the contents of the inventory from
    // before it was opened, to now after it being closed and applies a cooldown to slots with newly added items.
    private void processShowcaseUponClose(PlayerShowcase showcase, Inventory inventory) {
        ItemStack[] itemsBefore = showcase.getItems();
        ItemStack[] itemsAfter = inventory.getContents();

        for (int i = 0; i < itemsBefore.length; i++) {
            ItemStack itemBefore = itemsBefore[i];
            ItemStack itemAfter = itemsAfter[i];

            if (itemBefore == null || itemBefore.getType().isAir()) {
                if (itemAfter != null && !itemAfter.getType().isAir()) {
                    // Something has been added. Apply cooldown to the slot.
                    // TODO: Get the cooldown time from the config
                    long unlockTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5);
                    showcase.getSlotCooldowns().put(i, unlockTime);
                }
            }
        }

        // Save new showcase content.
        showcase.setItems(itemsAfter);
    }
}
