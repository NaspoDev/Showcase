package dev.naspo.showcase.datamanagement;

import dev.naspo.showcase.Showcase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Events implements Listener {

    // Owner tag used for block metadata.
    private final String METADATA_OWNER_TAG = "ownerUUID";
    // The text that should be written on a sign to link it with the player's showcase.
    private final String SIGN_SHOWCASE_LINK = "[Showcase]";
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

    // SIGN FEATURE
    // If the block placed is a sign, set its owner to the player who placed it in its metadata.
    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();

        if (block.getState() instanceof Sign) {
            block.setMetadata(METADATA_OWNER_TAG, new FixedMetadataValue(plugin, event.getPlayer().getUniqueId().toString()));
        }
    }

    // SIGN FEATURE
    // If "[Showcase]" is written on a sign, link the sign to the player's showcase.
    // (i.e. add it to their sign list).
    @EventHandler
    private void onSignChange(SignChangeEvent event) {
        for (String line : event.getLines()) {
            // If sign contains "[Showcase]"
            if (line.toLowerCase().contains(SIGN_SHOWCASE_LINK.toLowerCase())) {
                // If the writer of the sign has a showcase, add the sign location to their showcase data.
                if (Data.getShowcase(event.getPlayer().getUniqueId()) != null) {
                    Data.getShowcase(event.getPlayer().getUniqueId())
                            .addSign(event.getBlock().getLocation());
                }
            }
        }
    }

    // SIGN FEATURE
    // Open a showcase from a sign (right click on sign).
    @EventHandler
    private void onRightClick(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        // If the action was a right click on a block
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // If the block is a sign
            if (block.getState() instanceof Sign) {
                // If the sign has an "ownerUUD" metadata.
                if (block.hasMetadata(METADATA_OWNER_TAG)) {

                    // If the sign has "[Showcase]" written on it open the linked showcase.
                    for (String line : ((Sign) block.getState()).getLines()) {
                        if (line.toLowerCase().contains(SIGN_SHOWCASE_LINK.toLowerCase())) {
                            // Get the showcase. (Getting the owner's uuid from the metadata to pass in).
                            event.getPlayer().sendMessage(block.getMetadata(METADATA_OWNER_TAG).get(0).toString());
//                            PlayerShowcase showcase = Data.getShowcase(UUID.fromString(
//                                    block.getMetadata(METADATA_OWNER_TAG).get(0).toString()));

                            // If there is a showcase associated with that sign, open it.
//                            if (showcase != null) {
//                                showcase.openForPlayer(event.getPlayer());
//                            }
                        }
                    }
                }
            }
        }
    }

    // SIGN FEATURE
    // If the block broken is a showcase sign, remove it from the player's showcases' sign list.
    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getState() instanceof Sign) {
            if (block.hasMetadata(METADATA_OWNER_TAG)) {
                // Getting the showcase. (Getting UUID from block metadata).
                PlayerShowcase showcase = Data.getShowcase(UUID.fromString(
                        block.getMetadata(METADATA_OWNER_TAG).get(0).toString()));

                // If the sign owner does have a showcase, check if this sign is linked to it.
                if (showcase != null) {
                    if (showcase.getSignLocations().contains(block.getLocation())) {
                        showcase.removeSign(block.getLocation());
                    }
                }
            }
        }
    }
}
