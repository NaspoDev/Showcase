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

//Open showcase logic for all types of showcase opens. (Self-open, other online player open,
// other offline player open.
public class OpenShowcase {

    private final Permission VAULT_PERMS;
    private HashMap<Integer, Integer> showcaseSizes = new HashMap<>();

    private Showcase plugin;
    public OpenShowcase(Showcase plugin) {
        this.plugin = plugin;

        VAULT_PERMS = null;

        showcaseSizes.put(1, 9);
        showcaseSizes.put(2, 18);
        showcaseSizes.put(3, 27);
        showcaseSizes.put(4, 36);
        showcaseSizes.put(5, 46);
        showcaseSizes.put(6, 54);
    }

    //Open another (online) player's showcase.
    void openOthersOnlineInv(Player player, Player owner) {
        if (Data.invs.containsKey(owner.getUniqueId().toString())) {
            Inventory showcase = Bukkit.createInventory(owner, 9, owner.getName() + "'s Showcase");
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
            int showcaseSize = 9;

            if (vaultPerms.playerHas(null, owner, "showcase.size.2")) {
                showcaseSize = 18;
            }
            if (vaultPerms.playerHas(null, owner, "showcase.size.3")) {
                showcaseSize = 27;
            }
            if (vaultPerms.playerHas(null, owner, "showcase.size.4")) {
                showcaseSize = 36;
            }
            if (vaultPerms.playerHas(null, owner, "showcase.size.5")) {
                showcaseSize = 46;
            }
            if (vaultPerms.playerHas(null, owner, "showcase.size.6")) {
                showcaseSize = 54;
            }

            Inventory showcase = Bukkit.createInventory(null, 9, owner.getName() + "'s Showcase");
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
        //Check if they have special showcase size permissions.
        int showcaseSize = 9;

        if (player.hasPermission("showcase.size.2")) {
            showcaseSize = 18;
        }
        if (player.hasPermission("showcase.size.3")) {
            showcaseSize = 27;
        }
        if (player.hasPermission("showcase.size.4")) {
            showcaseSize = 36;
        }
        if (player.hasPermission("showcase.size.5")) {
            showcaseSize = 46;
        }
        if (player.hasPermission("showcase.size.6")) {
            showcaseSize = 54;
        }


        Inventory showcase = Bukkit.createInventory(player, showcaseSize, player.getName() + "'s Showcase");
        if (Data.invs.containsKey(player.getUniqueId().toString())) {
            showcase.setContents(Data.invs.get(player.getUniqueId().toString()));
        }
        player.openInventory(showcase);
    }
}
