package dev.naspo.showcase.models;

import dev.naspo.showcase.Showcase;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// Showcase Item data class.
public class ShowcaseItem {
    // The actual Minecraft item.
    private ItemStack item;
    // The initial time set (in seconds) before this item can be removed from the showcase.
    private final int initialCooldownSeconds;
    // The epoch time when this showcase item was added to the showcase.
    private long timeAddedEpoch;
    // Each showcase item should have a unique ID which is attached to it's itemstack.
    private UUID showcaseItemId;
    // The prefix for the cooldown lore that every showcase item with a cooldown will have.
    public static final String COOLDOWN_LORE_PREFIX = "Cooldown:";
    // The key used for the showcase item ID in the item's Persistent Data Container.
    public static final String SIID_KEY = "SIID";
    private BukkitTask cooldownLoreTask;
    private final Showcase plugin;

    public ShowcaseItem(ItemStack item, int cooldownSeconds, long timeAddedEpoch,
                        UUID showcaseItemId, Showcase plugin) {
        this.item = item;
        this.initialCooldownSeconds = cooldownSeconds;
        this.timeAddedEpoch = timeAddedEpoch;
        this.plugin = plugin;
        this.showcaseItemId = showcaseItemId;
        attachShowcaseItemId();

        // If there is a cooldown, start/continue updating the lore with the correct cooldown value.
        if (initialCooldownSeconds > 0) {
            repeatUpdateCooldownLore();
        }
    }

    // Set the showcase item id to the item's ItemMeta (as a Persistent Data Container).
    // Persistent data containers are saved when the entity unloads, hence "persistent".
    private void attachShowcaseItemId() {
        // The key for the Persistent Data. (SIID stands for showcase item ID).
        NamespacedKey key = new NamespacedKey(plugin, SIID_KEY);
        ItemMeta meta = item.getItemMeta();
        // If it doesn't already have an SIID, assign one.
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, showcaseItemId.toString());
            item.setItemMeta(meta);
        }
    }

    // Remove the showcase item id from the item's Persistent Data Container.
    private void removeShowcaseItemId() {
        NamespacedKey key = new NamespacedKey(plugin, SIID_KEY);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (pdc.has(key)) {
            pdc.remove(key);
//            item.setItemMeta(meta);
        }
    }

    public UUID getShowcaseItemId() {
        return showcaseItemId;
    }

    // Adds and continuously updates a cooldown countdown to the lore of the item.
    // Using Bukkit Scheduler to repeatedly update the lore every second.
    private void repeatUpdateCooldownLore() {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();

        // adding the countdown to the lore initially.
        lore.add(COOLDOWN_LORE_PREFIX + " " + getActiveCooldownValue() + "s");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        int loreIndex = lore.size() - 1; // it will be at the last index.

        // Next, after one second, start repeatedly counting down every second and update the lore.
        cooldownLoreTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // If the cooldown is no longer active, remove the cooldown countdown from the lore
            // and cancel this repeating task.
            if (getActiveCooldownValue() == 0) {
                lore.remove(loreIndex);
                this.cooldownLoreTask.cancel();
            }

            // Update the lore with the current cooldown value.
            lore.set(loreIndex, COOLDOWN_LORE_PREFIX + " " + getActiveCooldownValue() + "s");
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }, 20L, 20L);
    }

    // Call to manually remove cooldown lore and cancel lore update task.
    private void removeCooldownLore() {
        if (cooldownLoreTask != null) {
            cooldownLoreTask.cancel();
        }

        ItemMeta meta = item.getItemMeta();

        if (meta.hasLore()) {
            List<String> lore = meta.getLore();

            for (int i = 0; i < lore.size(); i++) {
                if (lore.get(i).contains(COOLDOWN_LORE_PREFIX)) {
                    lore.remove(i);
                    break;
                }
            }
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    public ItemStack getItem() {
        return item;
    }

    // Returns true if the item's cooldown still has time remaining.
    public boolean cooldownIsActive() {
        // Getting the amount of time (ms) that has elapsed since the item was first stored,
        // then converting to seconds.
        long timeElapsedInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeAddedEpoch);
        if (timeElapsedInSeconds >= initialCooldownSeconds) {
            return false;
        }
        return true;
    }

    // Utility function that returns true if the provided ItemStack has a cooldown remaining.
    // This is done by checking if the ItemStack has a cooldown countdown in its lore.
    // (It is assumed that an ItemStack from a showcase inventory is being passed in, but nothing bad
    // will happen if it isn't as we're just checking for lore contents).
    public static boolean cooldownIsActive(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta.hasLore()) {
            List<String> lore = itemMeta.getLore();
            if (lore.contains(COOLDOWN_LORE_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    // Returns the amount of seconds remaining on the cooldown. (Returns 0 if none).
    public int getActiveCooldownValue() {
        // Getting the amount of time (ms) that has elapsed since the item was first stored,
        // then converting to seconds.
        long timeElapsedInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeAddedEpoch);
        if (timeElapsedInSeconds >= initialCooldownSeconds) {
            return 0;
        } else {
            return initialCooldownSeconds - (int) timeElapsedInSeconds;
        }
    }

    public int getInitialCooldownSeconds() {
        return initialCooldownSeconds;
    }

    public long getTimeAddedEpoch() {
        return timeAddedEpoch;
    }

    // Removes any applicable showcase related lore, IDs, etc.
    // Typically used when the ShowcaseItem is being removed.
    public void cleanup() {
        removeCooldownLore();
        removeShowcaseItemId();
    }
}
