package dev.naspo.showcase.utils;

import dev.naspo.showcase.types.PlayerShowcase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

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

        String inventoryTitle = inventoryView.getTitle();
        String playerName = player.getName();
        return playerName.equalsIgnoreCase(inventoryTitle.substring(0, inventoryTitle.lastIndexOf("'")));
    }

    // Finds the showcase from the Inventory. Returns empty if the provided inventory is not a showcase inventory,
    // and therefore a showcase couldn't be found.
    public static Optional<PlayerShowcase> findShowcaseFromInventory(InventoryView inventoryView) {
        if (!isShowcaseInventory(inventoryView)) {
            return Optional.empty();
        }

        // First check online players.
        List<Player> onlinePlayers = new ArrayList<>();
        onlinePlayers.addAll(Bukkit.getOnlinePlayers());
        for (Player p : onlinePlayers) {
            if (p.getName().equalsIgnoreCase(showcaseOwnerName)) {
                showcaseOwnerUUID = p.getUniqueId();
            }
        }

        // If we still haven't found the showcase owner (i.e. the showcase owner wasn't online),
        // find them as an OfflinePlayer.
        if (showcaseOwnerUUID == null) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(showcaseOwnerName);
            showcaseOwnerUUID = p.getUniqueId();
        }
    }

    // Returns the UUID of the showcase owner by their name.
    public static UUID findShowcaseOwnerByName(String ownerName) {

    }
}
