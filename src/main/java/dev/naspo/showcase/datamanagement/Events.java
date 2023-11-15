package dev.naspo.showcase.datamanagement;

import dev.naspo.showcase.Showcase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Events implements Listener {

    // Owner tag used for block metadata.
    private final String METADATA_OWNER_TAG = "ownerUUID";
    private Showcase plugin;

    public Events(Showcase plugin) {
        this.plugin = plugin;
    }

    // If the player who clicked in the inventory is not the owner or does not have edit permissions,
    // cancel the event.
    @EventHandler
    private void onInvClick(InventoryClickEvent event) {
        String invTitle = event.getView().getTitle();
        if (invTitle.contains("'s Showcase")) {
            if (!(event.getWhoClicked().getName().equalsIgnoreCase(invTitle.substring(0, invTitle.lastIndexOf("'"))))) {
                if (!(event.getWhoClicked().hasPermission("showcase.edit"))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // Saving showcase contents on inv close.
    @EventHandler
    private void onInvClose(InventoryCloseEvent event) {
        String invTitle = event.getView().getTitle();

        if (invTitle.contains("'s Showcase")) {
            String invOwnerName = invTitle.substring(0, invTitle.lastIndexOf("'"));

            // If the owner of the showcase closed it, save the contents.
            if (event.getPlayer().getName().equalsIgnoreCase(invOwnerName)) {
                // Finding the showcase and setting the items.
                Data.getShowcase(event.getPlayer().getUniqueId()).setItems(event.getInventory().getContents());
                return;
            }

            // Otherwise, if someone with showcase edit perms closed it, save the contents.
            if (event.getPlayer().hasPermission("showcase.edit")) {
                UUID uuid;

                // Getting showcase owner UUID if they're online.
                List<Player> players = new ArrayList<>();
                players.addAll(Bukkit.getOnlinePlayers());
                for (Player p : players) {
                    if (invOwnerName.equalsIgnoreCase(p.getName())) {
                        uuid = Bukkit.getPlayer(invOwnerName).getUniqueId();
                        Data.getShowcase(uuid).setItems(event.getInventory().getContents());
                        return;
                    }
                }

                // If the showcase owner is offline...
                OfflinePlayer p = Bukkit.getOfflinePlayer(invOwnerName);
                uuid = p.getUniqueId();
                Data.getShowcase(uuid).setItems(event.getInventory().getContents());
            }
        }
    }

    // If the block placed is a sign, set its owner to the player who placed it in its metadata.
    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();

        if (block.getState() instanceof Sign) {
            block.setMetadata(METADATA_OWNER_TAG, new FixedMetadataValue(plugin, event.getPlayer().getUniqueId()));
        }
    }

    // TODO: Implement a listener to see if "[Showcase]" is written on a sign, then do what you gotta do...
}
