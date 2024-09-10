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
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, ShowcaseItem.SIID_KEY);
        // Get the showcase item ID. If it doesn't have one, it's not a showcase item.
        if (pdc.has(key)) {
            // Get the ShowcaseItem
            UUID SSID = UUID.fromString(pdc.get(key, PersistentDataType.STRING));
            ShowcaseItem showcaseItem = showcaseItems.get(SSID);
            showcaseItem.cleanup(item);
            showcaseItems.remove(SSID);
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
