package dev.naspo.showcase.models;

import dev.naspo.showcase.Showcase;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

// The PlayerShowcase object class.
// Holds and manages ShowcaseItems.
public class PlayerShowcase {
    // Holds this showcase's showcase items and their Showcase Item ID as the key.
    private final Showcase plugin;
    private List<ShowcaseItem> showcaseItems;

    public PlayerShowcase(Showcase plugin) {
        this.plugin = plugin;
        showcaseItems = new ArrayList<>();
    }

    // Add a new showcase item to this showcase.
    public void addShowcaseItem(ItemStack item, int cooldownSeconds) {
        long cooldownEndsEpoch = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(cooldownSeconds);
        showcaseItems.add(new ShowcaseItem(item, cooldownEndsEpoch, plugin));
    }

    // Adds a showcase item with a specified ending cooldown time.
    // Typically used when restoring data from player data file.
    public void addShowcaseItem(ItemStack item, long cooldownEndsEpoch) {
        showcaseItems.add(new ShowcaseItem(item, cooldownEndsEpoch, plugin));
    }

    // Remove a showcase item from the showcase.
    public void removeShowcaseItem(ItemStack item) {
        // First we have to find the showcase item out of all the showcase items in the list.
        // We can do this by matching the ItemStacks and the cooldown end time.
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, ShowcaseItem.COOLDOWN_ENDS_EPOCH_KEY);
        if (pdc.has(key)) {
            long cooldownEndsEpoch = pdc.get(key, PersistentDataType.LONG);

            for (int i = 0; i < showcaseItems.size(); i++) {
                ShowcaseItem showcaseItem = showcaseItems.get(i);
                if (showcaseItem.getItem().equals(item)) {
                    if (showcaseItem.getCooldownEndsEpoch() == cooldownEndsEpoch) {
                        showcaseItem.cleanup(item);
                        showcaseItems.remove(i);
                    }
                }
            }
        } else {
            // Log an error to the console if a cooldown ends key could not be found.
            plugin.getLogger().severe("Could not remove a showcase item from the showcase " +
                    "because it doesn't have a cooldown ending key in it's Persistent Data Container!");
        }
    }

    public List<ShowcaseItem> getShowcaseItems() {
        return showcaseItems;
    }

    // Returns the actual items (ItemStack) of the showcase items.
    public List<ItemStack> getShowcaseItemsRaw() {
        return showcaseItems.stream().map(ShowcaseItem::getItem).collect(Collectors.toList());
    }
}
