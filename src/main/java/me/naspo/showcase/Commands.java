package me.naspo.showcase;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Commands implements CommandExecutor {
    Showcase plugin;
    Data data;
    Commands(Showcase plugin, Data data) {
        this.plugin = plugin;
        this.data = data;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("showcase")) {
            //Console command stuff.
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
            //Player command stuff.
            Player player = (Player) sender;

            if (!(player.hasPermission("showcase.use"))) {
                player.sendMessage(Utils.chatColor(Utils.prefix +
                        plugin.getConfig().getString("messages.no-permission")));
                return true;
            }

            if (!(args.length == 0)) {
                if (args[0].equalsIgnoreCase("reload")) {
                    //Reload command.
                    if (!(player.hasPermission("showcase.reload"))) {
                        player.sendMessage(Utils.chatColor(Utils.prefix +
                                plugin.getConfig().getString("messages.no-permission")));
                        return true;
                    }
                    Utils.reloadConfigs();
                    player.sendMessage(Utils.chatColor(Utils.prefix +
                            plugin.getConfig().getString("messages.reload")));
                    return true;
                }
                //Help command.
                if (args[0].equalsIgnoreCase("help")) {
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
                    return true;
                }
                //Open another player's showcase.
                List<Player> players = new ArrayList<Player>();
                players.addAll(Bukkit.getOnlinePlayers());
                for (Player p : players) {
                    if (args[0].equalsIgnoreCase(p.getName().toLowerCase())) {
                        openOthersOnlineInv(player, p);
                        return true;
                    }
                }
                try {
                    if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                        OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                        openOthersOfflineInv(player, p);
                        return true;
                    }
                    player.sendMessage(Utils.chatColor(Utils.prefix +
                            "That players has never played on the server before."));
                    return true;
                } catch (Exception e) {
                    player.sendMessage(Utils.chatColor(Utils.prefix + "Unknown player."));
                }
                return true;
            }
            Inventory showcase = Bukkit.createInventory(player, 9, player.getName() + "'s Showcase");
            if (Data.invs.containsKey(player.getUniqueId().toString())) {
                showcase.setContents(Data.invs.get(player.getUniqueId().toString()));
            }
            player.openInventory(showcase);
        }
        return false;
    }

    //Open another (online) player's showcase.
    public void openOthersOnlineInv(Player player, Player owner) {
        if (Data.invs.containsKey(owner.getUniqueId().toString())) {
            Inventory showcase = Bukkit.createInventory(owner, 9, owner.getName() + "'s Showcase");
            showcase.setContents(Data.invs.get(owner.getUniqueId().toString()));
            player.openInventory(showcase);
            return;
        }
        player.sendMessage(Utils.chatColor(Utils.prefix + owner.getName() + " has not created a showcase."));
    }

    //Open another (offline) player's showcase.
    public void openOthersOfflineInv(Player player, OfflinePlayer owner) {
        if (Data.invs.containsKey(owner.getUniqueId().toString())) {
            Inventory showcase = Bukkit.createInventory(null, 9, owner.getName() + "'s Showcase");
            showcase.setContents(Data.invs.get(owner.getUniqueId().toString()));
            player.openInventory(showcase);
            return;
        }
        player.sendMessage(Utils.chatColor(Utils.prefix + owner.getName() + " has not created a showcase."));
    }
}
