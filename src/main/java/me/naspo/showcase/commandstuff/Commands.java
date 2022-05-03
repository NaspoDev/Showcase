package me.naspo.showcase.commandstuff;

import me.naspo.showcase.datamanagement.Data;
import me.naspo.showcase.Showcase;
import me.naspo.showcase.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor {
    Showcase plugin;
    Data data;
    public Commands(Showcase plugin, Data data) {
        this.plugin = plugin;
        this.data = data;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("showcase")) {

            // --- CONSOLE COMMAND STUFF ---

            if (!(sender instanceof Player)) {
                if (args.length == 0) {
                    sender.sendMessage("Did you mean /showcase reload?");
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    Utils.reloadConfigs();
                    sender.sendMessage("Showcase has been reloaded.");
                }
                return true;
            }

            // --- PLAYER COMMAND STUFF ---

            Player player = (Player) sender;

            //Basic permission check.
            if (!(player.hasPermission("showcase.use"))) {
                player.sendMessage(Utils.chatColor(Utils.prefix +
                        plugin.getConfig().getString("messages.no-permission")));
                return true;
            }

            if (!(args.length == 0)) {

                //Reload Command.
                if (args[0].equalsIgnoreCase("reload")) {
                    reloadCommand(player);
                    return true;
                }

                //Help command.
                if (args[0].equalsIgnoreCase("help")) {
                    helpCommand(player);
                    return true;
                }

                // --- Open Another Player's Showcase ---

                //Check if player is online.
                List<Player> players = new ArrayList<Player>();
                players.addAll(Bukkit.getOnlinePlayers());
                for (Player p : players) {
                    if (args[0].equalsIgnoreCase(p.getName().toLowerCase())) {
                        openOthersOnlineInv(player, p);
                        return true;
                    }
                }

                //Check if player is offline.
                try {
                    if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                        OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                        openOthersOfflineInv(player, p);
                        return true;
                    }
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                    player.sendMessage(Utils.chatColor(Utils.prefix +
                            Utils.placeholderPlayer(p,
                                    plugin.getConfig().getString("messages.player-has-never-joined"))));
                    return true;

                    //Player doesn't exist.
                } catch (Exception e) {
                    player.sendMessage(Utils.chatColor(Utils.prefix +
                            plugin.getConfig().getString("messages.unknown-player")));
                }
                return true;
            }

            //Open the player's own showcase.
            openOwnShowcase(player);
        }
        return false;
    }

    // --- PLAYER INDIVIDUAL COMMAND LOGIC ---

    private void reloadCommand(Player player) {
        if (!(player.hasPermission("showcase.reload"))) {

            player.sendMessage(Utils.chatColor(Utils.prefix +
                    plugin.getConfig().getString("messages.no-permission")));
            return;
        }
        Utils.reloadConfigs();
        player.sendMessage(Utils.chatColor(Utils.prefix +
                plugin.getConfig().getString("messages.reload")));
    }

    private void helpCommand(Player player) {
        String[] help = {
                "&5&lShowcase Help",
                "&5/showcase &7- Open your showcase.",
                "&5/showcase <player> &7- View another player's showcase.",
                "&5/showcase reload &7- Reload the plugin."
        };
        for (int i = 0; i <= 2; i++ ) {
            player.sendMessage(Utils.chatColor(help[i]));
        }
        if (player.hasPermission("showcase.reload")) {
            player.sendMessage(Utils.chatColor(help[3]));
        }
    }

    // --- OPEN SHOWCASE LOGIC ---

    //Open another (online) player's showcase.
    private void openOthersOnlineInv(Player player, Player owner) {
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
    private void openOthersOfflineInv(Player player, OfflinePlayer owner) {
        if (Data.invs.containsKey(owner.getUniqueId().toString())) {
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
    private void openOwnShowcase(Player player) {
        Inventory showcase = Bukkit.createInventory(player, 9, player.getName() + "'s Showcase");
        if (Data.invs.containsKey(player.getUniqueId().toString())) {
            showcase.setContents(Data.invs.get(player.getUniqueId().toString()));
        }
        player.openInventory(showcase);
    }
}
