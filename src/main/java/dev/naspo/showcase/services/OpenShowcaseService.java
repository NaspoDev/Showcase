package dev.naspo.showcase.services;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.datamanagement.DataManager;
import dev.naspo.showcase.types.PlayerShowcase;
import dev.naspo.showcase.utils.PlayerUtils;
import dev.naspo.showcase.utils.ShowcaseUtils;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.logging.Level;

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
    private DataManager dataManager;

    public OpenShowcaseService(Showcase plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
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
     *
     * @param viewer The player that will be opening the target's showcase.
     * @param target The player's showcase of which to open.
     */
    public void openOtherPlayerShowcase(Player viewer, Player target) {
        int showcaseSize = getShowcaseSize(target);
        PlayerShowcase showcase = dataManager.getPlayerShowcases().get(target.getUniqueId());
        ItemStack[] showcaseItems = showcase.getItems();

        // If there is a showcase mismatch error, log errors and exit.
        if (showcaseSizeMismatchExists(showcaseItems, showcaseSize)) {
            sendShowcaseSizeMismatchError(viewer, target.getName());
            return;
        }

        // Otherwise continue with the creation and opening of the inventory.
        // Create a blank showcase inventory with the target's information.
        Inventory inventory = Bukkit.createInventory(target, showcaseSize,
                target.getName() + ShowcaseUtils.SHOWCASE_INVENTORY_TITLE_SUFFIX);
        // Set its content's to the targets showcase contents.
        inventory.setContents(showcaseItems);
        // Open the target's showcase for the viewer.
        viewer.openInventory(inventory);
    }

    /**
     * Opens another offline player's showcase for the viewer.
     *
     * @param viewer The player that will be opening the target's showcase.
     * @param target The offline player's showcase of which to open.
     */
    public void openOtherPlayerShowcase(Player viewer, OfflinePlayer target) {
        int showcaseSize = getShowcaseSize(target);
        PlayerShowcase showcase = dataManager.getPlayerShowcases().get(target.getUniqueId());
        ItemStack[] showcaseItems = showcase.getItems();

        // If there is a showcase mismatch error, log errors and exit.
        if (showcaseSizeMismatchExists(showcaseItems, showcaseSize)) {
            sendShowcaseSizeMismatchError(viewer, target.getName());
            return;
        }

        // Otherwise continue with the creation and opening of the inventory.
        // Create a blank showcase inventory with the target's information.
        Inventory inventory = Bukkit.createInventory(null, showcaseSize,
                target.getName() + ShowcaseUtils.SHOWCASE_INVENTORY_TITLE_SUFFIX);
        // Set its content's to the targets showcase contents.
        inventory.setContents(showcaseItems);
        // Open the target's showcase for the viewer.
        viewer.openInventory(inventory);
    }

    /**
     * Open a player's own Showcase.
     *
     * @param player The player that will open their own showcase.
     */
    public void openOwnShowcase(Player player) {
        PlayerShowcase showcase = dataManager.getPlayerShowcases().get(player.getUniqueId());
        ItemStack[] showcaseItems = showcase.getItems();
        int showcaseSize = getShowcaseSize(player);

        // If there is a showcase mismatch error, log errors and exit.
        if (showcaseSizeMismatchExists(showcaseItems, showcaseSize)) {
            sendShowcaseSizeMismatchError(player, player.getName());
            return;
        }

        // Create a blank showcase inventory with the player's information.
        Inventory inventory = Bukkit.createInventory(player, showcaseSize,
                player.getName() + ShowcaseUtils.SHOWCASE_INVENTORY_TITLE_SUFFIX);

        // Synchronize cooldown lores.
        ShowcaseUtils.syncCooldownLores(showcaseItems, showcase.getSlotCooldowns());

        // Set the inventory contents and open it.
        inventory.setContents(showcaseItems);
        player.openInventory(inventory);
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

    // Returns true if there are too many showcase items for the allotted showcase inventory size.
    private boolean showcaseSizeMismatchExists(ItemStack[] showcaseItems, int showcaseSize) {
        return showcaseItems.length > showcaseSize;
    }

    /**
     * Sends a showcase size mismatch error message to the player, and
     * logs a detailed error message to the console.
     * <p>
     * The showcase size mismatch error occurs when a player had their showcase size reduced,
     * causing an error when too many items are trying to be put into an inventory too small.
     *
     * @param viewer           The player trying to view the showcase.
     * @param targetPlayerName The name of the player with the showcase size mismatch. (Can be same as viewer if
     *                         the player is trying to open their own showcase).
     */
    private void sendShowcaseSizeMismatchError(Player viewer, String targetPlayerName) {
        // Send brief error message to the viewer.
        // Send a slightly differently worded message if the viewer is also the target.
        if (viewer.getName().equalsIgnoreCase(targetPlayerName)) {
            PlayerUtils.sendFormattedMessage(plugin, viewer,
                    "Error opening your showcase. Contact a server administrator for help.");
        } else {
            PlayerUtils.sendFormattedMessage(plugin, viewer,
                    "Error opening " + targetPlayerName + "'s showcase. " +
                            "Contact a server administrator for help.");
        }

        // Console error logging.
        String showcaseSizeMismatchErrorMsg = """
                Cannot open showcase for player %s because they have too many things saved in their showcase and 
                not a big enough showcase to contain them! 
                
                This probably happened because their 'showcase.size' permission was reduced.
                
                To fix this, either re-increase their 'showcase.size' or manually delete enough items
                from their Showcase player data file.
                """.formatted(targetPlayerName);
        plugin.getLogger().log(Level.SEVERE, showcaseSizeMismatchErrorMsg);
    }
}
