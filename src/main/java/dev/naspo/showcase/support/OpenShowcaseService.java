package dev.naspo.showcase.support;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.data.DataManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

// Open showcase logic for all types of showcase opens. (Self-open, other online player open,
// other offline player open). A service class.
public class OpenShowcaseService {

    private Permission vaultPerms; //Vault permission API handler.
    private HashMap<String, Integer> showcaseSizes = new HashMap<>(); //Showcase size perms and inv value equivalent.

    private final Showcase plugin;
    private DataManager dataManager;

    public OpenShowcaseService(Showcase plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;

        vaultPerms = null;

        showcaseSizes.put("showcase.size.2", 18);
        showcaseSizes.put("showcase.size.3", 27);
        showcaseSizes.put("showcase.size.4", 36);
        showcaseSizes.put("showcase.size.5", 46);
        showcaseSizes.put("showcase.size.6", 54);
    }

    // Open another (online) player's showcase.
    public void openOthersOnlineInv(Player player, Player owner) {
        // Create a blank showcase inventory with the owner's information.
        Inventory showcase = Bukkit.createInventory(owner, getShowcaseSize(owner), owner.getName() + "'s Showcase");
        // Set its content's to the owners showcase contents.
        showcase.setContents(dataManager.invs.get(owner.getUniqueId().toString()));
        // Open the owner's showcase for the player.
        player.openInventory(showcase);
    }

    // Open another (offline) player's showcase.
    public void openOthersOfflineInv(Player player, OfflinePlayer owner) {
        // Create a blank showcase inventory with the owner's information.
        Inventory showcase = Bukkit.createInventory(null, getShowcaseSize(owner),
                owner.getName() + "'s Showcase");
        // Set its content's to the owners showcase contents.
        showcase.setContents(dataManager.invs.get(owner.getUniqueId().toString()));
        // Open the owner's showcase for the player.
        player.openInventory(showcase);
    }

    //Open player's own showcase.
    public void openOwnShowcase(Player player) {
        // Create a blank showcase inventory with the player's information.
        Inventory showcase = Bukkit.createInventory(player, getShowcaseSize(player),
                player.getName() + "'s Showcase");
        // Set its contents to their showcase's contents.
        showcase.setContents(dataManager.invs.get(player.getUniqueId().toString()));
        player.openInventory(showcase); // Open their showcase.
    }

    // Returns the size of an online player's showcase size.
    // If they have no other showcase size permission, return the default size of 9.
    private int getShowcaseSize(Player player) {
        int showcaseSize = 9;

        for (Map.Entry<String, Integer> entry : showcaseSizes.entrySet()) {
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

        for (Map.Entry<String, Integer> entry : showcaseSizes.entrySet()) {
            if (vaultPerms.playerHas(null, player, entry.getKey())) {
                showcaseSize = entry.getValue();
            }
        }

        return showcaseSize;
    }
}
