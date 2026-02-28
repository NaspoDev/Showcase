package dev.naspo.showcase.utils;

import dev.naspo.showcase.types.PlayerShowcase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Showcase related utility.
public class ShowcaseUtils {

    // A showcase inventory will always end with this text.
    private static final String SHOWCASE_INVENTORY_TITLE_SUFFIX = "'s Showcase";
    // Item cooldown lore always starts with this text.
    private static final String COOLDOWN_LORE_PREFIX = "Cooldown: ";

    // Returns true if the specified inventory is a showcase inventory.
    public static boolean isShowcaseInventory(InventoryView inventoryView) {
        return inventoryView.getTitle().endsWith(SHOWCASE_INVENTORY_TITLE_SUFFIX);
    }

    // Returns true if the specified showcase inventory belongs to the specified player.
    // Checks vis InventoryView.
    public static boolean showcaseBelongsTo(InventoryView inventoryView, Player player) {
        // Makes sure the InventoryView passed in is a showcase inventory.
        if (!isShowcaseInventory(inventoryView)) {
            return false;
        }

        String playerName = player.getName();
        return playerName.equalsIgnoreCase(getShowcaseOwnerNameFromInvTitle(inventoryView));
    }

    /**
     * Parses and returns a showcase inventory title to get the owner's name.
     *
     * @param inventoryView The Inventor
     * @return The name of the showcase owner. Returns null if the provided InventoryView is not a showcase inventory.
     */
    public static String getShowcaseOwnerNameFromInvTitle(InventoryView inventoryView) {
        if (!isShowcaseInventory(inventoryView)) {
            return null;
        } else {
            String invTitle = inventoryView.getTitle();
            return invTitle.substring(0, invTitle.lastIndexOf("'"));
        }
    }

    // Returns true if the player has permission to edit the showcase.
    // Checks via inventory title.
    public static boolean playerCanEditShowcase(InventoryView inventoryView, Player player) {
        // If they have the edit permission, or it's their showcase and they have the "use" permission, return true.
        return player.hasPermission("showcase.edit")
                || (ShowcaseUtils.showcaseBelongsTo(inventoryView, player) && player.hasPermission("showcase.use"));
    }

    // Adds cooldown lore to an item.
    public static void addCooldownLore(ItemStack item, long unlockTime) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add("Cooldown time remaining: " + (unlockTime - System.currentTimeMillis()));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    // Removes cooldown lore from item lore.
    public static void removeCooldownLore(List<String> lore) {
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).startsWith(COOLDOWN_LORE_PREFIX)) {
                lore.remove(i);
            }
        }
    }
}
