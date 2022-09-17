package me.naspo.showcase.commandstuff;

import me.naspo.showcase.Showcase;
import me.naspo.showcase.Utils;
import me.naspo.showcase.datamanagement.Data;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

//Open showcase logic for all types of showcase opens. (Self-open, other online player open,
// other offline player open.
public class OpenShowcase {

    private Permission vaultPerms; //Vault permission API handler.
    private HashMap<String, Integer> showcaseSizes = new HashMap<>(); //Showcase size perms and inv value equivalent.


    private Showcase plugin;
    public OpenShowcase(Showcase plugin) {
        this.plugin = plugin;

        vaultPerms = null;

        showcaseSizes.put("showcase.size.2", 18);
        showcaseSizes.put("showcase.size.3", 27);
        showcaseSizes.put("showcase.size.4", 36);
        showcaseSizes.put("showcase.size.5", 46);
        showcaseSizes.put("showcase.size.6", 54);
    }

    //Open another (online) player's showcase.
    void openOthersOnlineInv(Player player, Player owner) {
        if (Data.invs.containsKey(owner.getUniqueId().toString())) {
            Inventory showcase = Bukkit.createInventory(owner, getShowcaseSize(owner), owner.getName() + "'s Showcase");
            showcase.setContents(Data.invs.get(owner.getUniqueId().toString()));
            player.openInventory(showcase);
            return;
        }
        player.sendMessage(Utils.chatColor(Utils.prefix +
                Utils.placeholderPlayer(owner,
                        plugin.getConfig().getString("messages.player-not-created-showcase"))));
    }

    //Open another (offline) player's showcase.
    void openOthersOfflineInv(Player player, OfflinePlayer owner) {
        if (Data.invs.containsKey(owner.getUniqueId().toString())) {
            Inventory showcase = Bukkit.createInventory(null, getShowcaseSize(owner),
                    owner.getName() + "'s Showcase");
            showcase.setContents(Data.invs.get(owner.getUniqueId().toString()));
            player.openInventory(showcase);
            return;
        }
        player.sendMessage(Utils.chatColor(Utils.prefix +
                Utils.placeholderPlayer(owner,
                        plugin.getConfig().getString("messages.player-not-created-showcase"))));
    }

    //Open player's own showcase.
    void openOwnShowcase(Player player) {
        Inventory showcase = Bukkit.createInventory(player, getShowcaseSize(player),
                player.getName() + "'s Showcase");

        if (Data.invs.containsKey(player.getUniqueId().toString())) {
            showcase.setContents(Data.invs.get(player.getUniqueId().toString()));
        }
        player.openInventory(showcase);
    }

    // --- Get Player's Showcase Size ---
    //Returns the size of the player in question's showcase size.

    //(For online player)
    private int getShowcaseSize(Player player) {
        int showcaseSize = 9;

        for (Map.Entry<String, Integer> entry : showcaseSizes.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                showcaseSize = entry.getValue();
            }
        }

        return showcaseSize;
    }

    //(For offline player)
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
