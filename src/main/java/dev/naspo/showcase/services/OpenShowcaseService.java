package dev.naspo.showcase.services;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.datamanagement.Data;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

// Open showcase logic for all types of showcase opens. (Self-open, other online player open,
// other offline player open).
public class OpenShowcaseService {

    // Vault permission API.
    private Permission vaultPerms;
    // Showcase size permissions to their respective inventory size.
    private final HashMap<String, Integer> permissionToSize = new HashMap<>();
    private Showcase plugin;

    public OpenShowcaseService(Showcase plugin) {
        this.plugin = plugin;

        vaultPerms = null;

        // Defining showcase size permissions and values.
        permissionToSize.put("showcase.size.2", 18);
        permissionToSize.put("showcase.size.3", 27);
        permissionToSize.put("showcase.size.4", 36);
        permissionToSize.put("showcase.size.5", 46);
        permissionToSize.put("showcase.size.6", 54);
    }


    /**
     * Opens another online player's showcase for the viewer.
     * @param viewer The player that will be opening the target's showcase.
     * @param target The player's showcase of which to open.
     */
    public void openOtherPlayerShowcase(Player viewer, Player target) {
        // Create a blank showcase inventory with the target's information.
        Inventory showcase = Bukkit.createInventory(target, getShowcaseSize(target), target.getName() + "'s Showcase");
        // Set its content's to the targets showcase contents.
        showcase.setContents(Data.invs.get(target.getUniqueId().toString()));
        // Open the target's showcase for the viewer.
        viewer.openInventory(showcase);
    }

    /**
     * Opens another offline player's showcase for the viewer.
     * @param viewer The player that will be opening the target's showcase.
     * @param target The offline player's showcase of which to open.
     */
    public void openOtherPlayerShowcase(Player viewer, OfflinePlayer target) {
        // Create a blank showcase inventory with the target's information.
        Inventory showcase = Bukkit.createInventory(null, getShowcaseSize(target),
                target.getName() + "'s Showcase");
        // Set its content's to the targets showcase contents.
        showcase.setContents(Data.invs.get(target.getUniqueId().toString()));
        // Open the target's showcase for the viewer.
        viewer.openInventory(showcase);
    }

    /**
     * Open a player's own Showcase.
     * @param player The player that will open their own showcase.
     */
    public void openOwnShowcase(Player player) {
        // Create a blank showcase inventory with the player's information.
        Inventory showcase = Bukkit.createInventory(player, getShowcaseSize(player),
                player.getName() + "'s Showcase");
        // Set its contents to their showcase's contents.
        showcase.setContents(Data.invs.get(player.getUniqueId().toString()));
        player.openInventory(showcase); // Open their showcase.
    }

    // Returns the size of an online player's showcase size.
    // If they have no other showcase size permission, return the default size of 9.
    private int getShowcaseSize(Player player) {
        int showcaseSize = 9;

        for (Map.Entry<String, Integer> entry : permissionToSize.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                showcaseSize = entry.getValue();
            }
        }

        return showcaseSize;
    }

    // Returns the size of an offline player's showcase size.
    // If they have no other showcase size permission, return the default size of 9.
    private int getShowcaseSize(OfflinePlayer player) {
        int showcaseSize = 9;

        for (Map.Entry<String, Integer> entry : permissionToSize.entrySet()) {
            if (vaultPerms.playerHas(null, player, entry.getKey())) {
                showcaseSize = entry.getValue();
            }
        }

        return showcaseSize;
    }
}
