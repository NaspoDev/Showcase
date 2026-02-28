package dev.naspo.showcase.utils;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.types.PlayerShowcase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;

// Showcase related utility.
public class ShowcaseUtils {

    // A showcase inventory will always end with this text.
    public static final String SHOWCASE_INVENTORY_TITLE_SUFFIX = "'s Showcase";
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

    // Synchronizes cooldown lore on a set of showcase items. (Adds, updates, or removes where needed).
    // Will remove cooldown lore if the cooldowns feature is not enabled.
    public static void syncCooldownLores(ItemStack[] showcaseItems, HashMap<Integer, Long> slotCooldowns, Showcase plugin) {
        // If cooldowns aren't enabled, remove cooldown lore from items.
        if (!Utils.cooldownsFeatureIsEnabled(plugin)) {
            for (ItemStack item : showcaseItems) {
                removeCooldownLore(item);
            }
            return;
        }

        // Otherwise the cooldowns feature is enabled, so sync each item's cooldown lore accordingly.
        for (int i = 0; i < showcaseItems.length; i++) {
            ItemStack item = showcaseItems[i];
            if (item != null) {
                syncCooldownLore(item, slotCooldowns.get(i));
            }
        }
    }

    // Updates cooldown lore on a single item. (Adds, updates, or removes where needed).
    // TODO: set cooldown time remaining in a proper time format
    public static void syncCooldownLore(ItemStack item, long unlockTime) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        long currentTime = System.currentTimeMillis();
        long timeRemaining = unlockTime - currentTime;

        int idxOfCooldownLore = -1; // Index of cooldown lore in lore list. (-1 means it doesn't exist).
        // Find existing cooldown lore if it exists.
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).startsWith(COOLDOWN_LORE_PREFIX)) {
                idxOfCooldownLore = i;
                break;
            }
        }

        // If there is an active cooldown...
        if (timeRemaining > 0) {
            String cooldownLine = COOLDOWN_LORE_PREFIX + timeRemaining;
            // If there is no existing cooldown lore, add it.
            if (idxOfCooldownLore == -1) {
                lore.add(cooldownLine);
            } else {
                // Otherwise update the existing cooldown lore.
                lore.set(idxOfCooldownLore, cooldownLine);
            }
        } else {
            // There is no cooldown active. Remove cooldown lore if it exists.
            if (idxOfCooldownLore >= 0) {
                lore.remove(idxOfCooldownLore);
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    // Removes cooldown lore from an item.
    public static void removeCooldownLore(ItemStack item) {
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

    public static long getCooldownDuration(Showcase plugin) {
        int cooldownDurationMinutes = plugin.getConfig().getInt("cooldowns.duration-minutes");
        return TimeUnit.MINUTES.toMillis(cooldownDurationMinutes);
    }
}
