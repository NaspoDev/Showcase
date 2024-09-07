package dev.naspo.showcase.models;

import dev.naspo.showcase.Showcase;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.N;

import java.util.HashMap;
import java.util.UUID;

// The PlayerShowcase object class.
// Holds and manages ShowcaseItems.
public class PlayerShowcase {
    // Holds this showcase's showcase items and their Showcase Item ID as the key.
    private HashMap<UUID, ShowcaseItem> showcaseItems;
    private final Showcase plugin;

    public PlayerShowcase(Showcase plugin) {
        this.plugin = plugin;
    }

    // Add a new showcase item to this showcase.
    public void addShowcaseItem(ItemStack item, int cooldownSeconds) {
        UUID showcaseItemId = UUID.randomUUID();
        ShowcaseItem showcaseItem = new ShowcaseItem(item, cooldownSeconds,
                System.currentTimeMillis(), showcaseItemId, plugin);
        showcaseItems.put(showcaseItemId, showcaseItem);
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
            showcaseItem.cleanup();
            showcaseItems.remove(SSID);
        }
    }
}
