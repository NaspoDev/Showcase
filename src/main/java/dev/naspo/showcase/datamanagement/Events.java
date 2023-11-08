package dev.naspo.showcase.datamanagement;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;

public class Events implements Listener {

    //Manages edit permissions for a showcase when one is opened.
    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        String invTitle = event.getView().getTitle();
        if (invTitle.contains("'s Showcase")) {
            if (!(event.getWhoClicked().getName().equalsIgnoreCase(invTitle.substring(0, invTitle.lastIndexOf("'"))))) {
                if (!(event.getWhoClicked().hasPermission("showcase.edit"))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    //Saving showcase contents to HashMap on inv close.
    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        String invTitle = event.getView().getTitle();

        if (invTitle.contains("'s Showcase")) {

            //If the owner of the showcase closed it, save the contents.
            String invOwnerName = invTitle.substring(0, invTitle.lastIndexOf("'"));
            if (event.getPlayer().getName().equalsIgnoreCase(invOwnerName)) {
                Data.invs.put(event.getPlayer().getUniqueId().toString(), event.getInventory().getContents());
                return;
            }

            //Otherwise, if someone with showcase edit perms closed it, save the contents.
            if (event.getPlayer().hasPermission("showcase.edit")) {
                String uuid;
                List<Player> players = new ArrayList<>();
                players.addAll(Bukkit.getOnlinePlayers());
                for (Player p : players) {
                    if (invOwnerName.equalsIgnoreCase(p.getName())) {
                        uuid = Bukkit.getPlayer(invOwnerName).getUniqueId().toString();
                        Data.invs.put(uuid, event.getInventory().getContents());
                        return;
                    }
                }
                OfflinePlayer p = Bukkit.getOfflinePlayer(invOwnerName);
                uuid = p.getUniqueId().toString();
                Data.invs.put(uuid, event.getInventory().getContents());
            }
        }
    }
}
