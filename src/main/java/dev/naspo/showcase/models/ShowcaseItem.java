package dev.naspo.showcase.models;

import dev.naspo.showcase.Showcase;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// Showcase Item data class.
public class ShowcaseItem {
    // The actual Minecraft item.
    private ItemStack item;
    // The epoch time when the cooldown should end for this showcase item.
    private final long cooldownEndsEpoch;


    // The prefix for the cooldown lore that every showcase item with a cooldown will have.
    public static final String COOLDOWN_LORE_PREFIX = "Cooldown:";
    // The key used for the cooldown ends time in the item's Persistent Data Container.
    public static final String COOLDOWN_ENDS_EPOCH_KEY = "cooldownEnds";
    private BukkitTask cooldownLoreTask;
    private final Showcase plugin;

    public ShowcaseItem(ItemStack item, long cooldownEndsEpoch, Showcase plugin) {
        this.item = item;
        this.cooldownEndsEpoch = cooldownEndsEpoch;
        this.plugin = plugin;
        attachCooldownEndsKey();

        // If there is a cooldown, start the lore updating task.
        if (System.currentTimeMillis() < cooldownEndsEpoch) {
            repeatUpdateCooldownLore();
        }
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    // Set cooldown ends value to the item's Persistent Data Container.
    // Persistent data containers are saved when the entity unloads, hence "persistent".
    private void attachCooldownEndsKey() {
        NamespacedKey key = new NamespacedKey(plugin, COOLDOWN_ENDS_EPOCH_KEY);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        // If it doesn't already have a cooldown ends value, assign one.
        if (!pdc.has(key)) {
            pdc.set(key, PersistentDataType.LONG, cooldownEndsEpoch);
            item.setItemMeta(meta);
        }
    }

    // Remove the cooldown ends value from the item's Persistent Data Container.
    private void removeCooldownEndsKey(ItemStack item) {
        NamespacedKey key = new NamespacedKey(plugin, COOLDOWN_ENDS_EPOCH_KEY);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (pdc.has(key)) {
            pdc.remove(key);
            item.setItemMeta(meta);
        }
    }

    // Adds and continuously updates a cooldown countdown to the lore of the item.
    // Using Bukkit Scheduler to repeatedly update the lore every second.
    private void repeatUpdateCooldownLore() {
        ItemMeta itemMeta = item.getItemMeta();
        final List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();

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

            // Format time remaining on cooldown.
            int activeCooldownValue = getActiveCooldownValue();
            int days = (int) TimeUnit.SECONDS.toDays(activeCooldownValue);
            // Subtraction at the end is done to account to time values already calculate for, as the first
            // step just converts the full time amount to the desired TimeUnit.
            long hours = TimeUnit.SECONDS.toHours(activeCooldownValue) - ((long) days * 24);
            long minutes = TimeUnit.SECONDS.toMinutes(activeCooldownValue) - (TimeUnit.SECONDS.toHours(activeCooldownValue) * 60);
            long seconds = activeCooldownValue - (TimeUnit.SECONDS.toMinutes(activeCooldownValue) * 60);

            String formattedCooldownTime = "";
            if (days > 0) {
                formattedCooldownTime += days + "d" + " ";
            }
            if (hours > 0) {
                formattedCooldownTime += hours + "h" + " ";
            }
            if (minutes > 0) {
                formattedCooldownTime += minutes + "m" + " ";
            }
            if (seconds > 0) {
                formattedCooldownTime += seconds + "s" + " ";
            }
            formattedCooldownTime = formattedCooldownTime.trim();

            // Update the lore with the current cooldown value.
            lore.set(loreIndex, COOLDOWN_LORE_PREFIX + " " + formattedCooldownTime);
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }, 20L, 20L);
    }

    // Call to manually remove cooldown lore and cancel lore update task.
    public void removeCooldownLore() {
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
            item.setItemMeta(meta);
        }
    }

    // Returns true if the item's cooldown still has time remaining.
    public boolean cooldownIsActive() {
        return System.currentTimeMillis() < cooldownEndsEpoch;
    }

    // Utility function that returns true if the provided ItemStack has a cooldown remaining.
    // This is done by checking if the ItemStack has a cooldown countdown in its lore.
    // (It is assumed that an ItemStack from a showcase inventory is being passed in, but nothing bad
    // will happen if it isn't as we're just checking for lore contents).
    public static boolean cooldownIsActive(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta.hasLore()) {
            for (String s : itemMeta.getLore()) {
                if (s.contains(COOLDOWN_LORE_PREFIX)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Returns the amount of seconds remaining on the cooldown. (Returns 0 if none).
    public int getActiveCooldownValue() {
        int activeCooldownValue = (int) TimeUnit.MILLISECONDS.toSeconds(cooldownEndsEpoch - System.currentTimeMillis());
        return (activeCooldownValue > 0) ? activeCooldownValue : 0;
    }

    public long getCooldownEndsEpoch() {
        return cooldownEndsEpoch;
    }

    // Removes any applicable showcase related lore, keys, etc.
    // Typically used when the ShowcaseItem is being removed.
    public void cleanup(ItemStack item) {
        removeCooldownLore();
        removeCooldownEndsKey(item);
    }
}
