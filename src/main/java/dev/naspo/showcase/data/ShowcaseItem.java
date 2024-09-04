package dev.naspo.showcase.data;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.TimeUnit;

// Showcase Item data class.
public class ShowcaseItem {
    // The actual Minecraft item.
    private ItemStack item;
    // The time in seconds before this item can be removed from the showcase.
    private int cooldownSeconds;
    // The epoch time when this showcase item was added to the showcase.
    private long timeAddedEpoch;
    // The prefix for the cooldown lore that every showcase item with a cooldown will have.
    public static final String COOLDOWN_LORE_PREFIX = "Cooldown:";


    public ShowcaseItem(ItemStack item, int cooldownSeconds, long timeAddedEpoch) {
        this.item = item;
        this.cooldownSeconds = cooldownSeconds;
        this.timeAddedEpoch = timeAddedEpoch;

        // If there is a cooldown, set cooldown lore for the item.
        if (cooldownSeconds > 0) {
            setCooldownLore();
        }
    }

    // Adds cooldown countdown to the lore of the item.
    // Using Bukkit Scheduler to repeatedly update the lore every second.
    private void setCooldownLore() {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();

        Bukkit.getScheduler().schedule
        lore.add(COOLDOWN_LORE_PREFIX + " ")
    }

    public ItemStack getItem() {
        return item;
    }

    // Returns true if the item can be removed based on the cooldown.
    public boolean canRemove() {
        // Getting the amount of time (ms) that has elapsed since the item was first stored,
        // then converting to seconds.
        long timeElapsedInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeAddedEpoch);
        if (timeElapsedInSeconds >= cooldownSeconds) {
            return true;
        }
        return false;
    }
}
