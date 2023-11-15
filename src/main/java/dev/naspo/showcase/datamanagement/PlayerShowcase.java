package dev.naspo.showcase.datamanagement;

import dev.naspo.showcase.Utils;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Player showcase object class
public class PlayerShowcase {

    // PlayerShowcase object related variables
    private final UUID OWNER_UUID;
    private ItemStack[] items; // items in the showcase
    private ArrayList<Location> signLocations; // locations of associated clickable signs with this showcase

    // Showcase size permissions and inventory size equivalent.
    private HashMap<String, Integer> showcaseSizes;
    private Permission vaultPerms; //Vault permission API handler.

    public PlayerShowcase(UUID OWNER_UUID, ItemStack[] items) {
        this.OWNER_UUID = OWNER_UUID;
        this.items = items;
        signLocations = new ArrayList<>();

        // Adding showcase permissions and respective sizes
        showcaseSizes = new HashMap<>();
        showcaseSizes.put("showcase.size.2", 18);
        showcaseSizes.put("showcase.size.3", 27);
        showcaseSizes.put("showcase.size.4", 36);
        showcaseSizes.put("showcase.size.5", 46);
        showcaseSizes.put("showcase.size.6", 54);
    }

    // Adds a new sign to the sign list
    public void addSign(Location location) {
        signLocations.add(location);
    }

    // Removes a sign from the sign list
    public void removeSign(Location location) {
        signLocations.remove(location);
    }

    // Getters

    public UUID getOwnerUUID() {
        return OWNER_UUID;
    }

    public ItemStack[] getItems() {
        return items;
    }

    // Create and return an Inventory of this showcase.
    public Inventory getShowcaseInv() {
        Inventory showcase;

        // If owner is online
        if (Utils.getPlayer(OWNER_UUID) != null) {
            showcase = Bukkit.createInventory(Utils.getPlayer(OWNER_UUID), getShowcaseSize(),
                    Utils.getPlayer(OWNER_UUID).getName() + "'s Showcase");
            // if the owner is offline
        } else {
            showcase = Bukkit.createInventory(Utils.getPlayer(OWNER_UUID), getShowcaseSize(),
                    Bukkit.getOfflinePlayer(OWNER_UUID).getName() + "'s Showcase");
        }

        showcase.setContents(items);
        return showcase;
    }

    public ArrayList<Location> getSignLocations() {
        return signLocations;
    }

    // Returns the size of this showcase
    public int getShowcaseSize() {
        int showcaseSize = 9;

        // If the owner is online
        if (Utils.getPlayer(OWNER_UUID) != null) {
            for (Map.Entry<String, Integer> entry : showcaseSizes.entrySet()) {
                if (Utils.getPlayer(OWNER_UUID).hasPermission(entry.getKey())) {
                    showcaseSize = entry.getValue();
                }
            }
            // Else if the owner is offline
        } else {
            for (Map.Entry<String, Integer> entry : showcaseSizes.entrySet()) {
                if (vaultPerms.playerHas(null, Bukkit.getOfflinePlayer(OWNER_UUID), entry.getKey())) {
                    showcaseSize = entry.getValue();
                }
            }
        }

        return showcaseSize;
    }

    // Setters

    public void setItems(ItemStack[] items) {
        this.items = items;
    }

}
