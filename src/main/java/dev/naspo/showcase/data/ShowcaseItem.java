package dev.naspo.showcase.data;

import dev.naspo.showcase.Showcase;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.TimeUnit;

// Showcase Item data class.
public class ShowcaseItem {
    // The actual Minecraft item.
    private ItemStack item;
    // The initial time set (in seconds) before this item can be removed from the showcase.
    private final int initialCooldownSeconds;
    // The epoch time when this showcase item was added to the showcase.
    private long timeAddedEpoch;
    private final Showcase plugin;
    // The prefix for the cooldown lore that every showcase item with a cooldown will have.
    public static final String COOLDOWN_LORE_PREFIX = "Cooldown:";

    public ShowcaseItem(ItemStack item, int cooldownSeconds, long timeAddedEpoch, Showcase plugin) {
        this.item = item;
        this.initialCooldownSeconds = cooldownSeconds;
        this.timeAddedEpoch = timeAddedEpoch;
        this.plugin = plugin;

        // If there is a cooldown, start/continue updating the lore with the correct cooldown value.
        if (initialCooldownSeconds > 0) {
            repeatUpdateCooldownLore();
        }
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
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            lore.set(loreIndex, COOLDOWN_LORE_PREFIX + " " + getActiveCooldownValue() + "s");
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }, 20L, 20L);
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
}
