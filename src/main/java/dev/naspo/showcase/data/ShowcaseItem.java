package dev.naspo.showcase.data;

import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

// Showcase Item data class.
public class ShowcaseItem {
    // The actual Minecraft item.
    private ItemStack item;
    // The time in seconds before this item can be removed from the showcase.
    private int cooldownSeconds;
    // The epoch time when this showcase item was added to the showcase.
    private long timeAddedEpoch;

    public ShowcaseItem(ItemStack item, int cooldownSeconds, long timeAddedEpoch) {
        this.item = item;
        this.cooldownSeconds = cooldownSeconds;
        this.timeAddedEpoch = timeAddedEpoch;
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
