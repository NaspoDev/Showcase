package dev.naspo.showcase.commandstuff;

import dev.naspo.showcase.datamanagement.Data;
import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.Utils;
import dev.naspo.showcase.datamanagement.PlayerShowcase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor {

    private Showcase plugin;
    public Commands(Showcase plugin) {
        this.plugin = plugin;
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

            // Basic permission check.
            if (!(player.hasPermission("showcase.use"))) {
                player.sendMessage(Utils.chatColor(Utils.prefix +
                        plugin.getConfig().getString("messages.no-permission")));
                return true;
            }

            if (!(args.length == 0)) {

                // Reload Command.
                if (args[0].equalsIgnoreCase("reload")) {
                    reloadCommand(player);
                    return true;
                }

                // Help command.
                if (args[0].equalsIgnoreCase("help")) {
                    helpCommand(player);
                    return true;
                }

                // --- Open Another Player's Showcase ---

                // If the other player is online, open their showcase.
                for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                    if (args[0].equalsIgnoreCase(otherPlayer.getName().toLowerCase())) {
                        // if the other player has a showcase, open it.
                        if (Data.getShowcase(otherPlayer.getUniqueId()) != null) {
                            Data.getShowcase(otherPlayer.getUniqueId()).openForPlayer(player);
                            // if they don't have a showcase, send an error message.
                        } else {
                            player.sendMessage(Utils.chatColor(Utils.prefix +
                                    Utils.placeholderPlayer(otherPlayer,
                                            plugin.getConfig().getString("messages.player-not-created-showcase"))));
                        }
                        return true;
                    }
                }

                // If other player is offline, open their showcase.
                try {
                    // If the offline player is real and has played before.
                    if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                        OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(args[0]);
                        // if the other player has a showcase, open it.
                        if (Data.getShowcase(otherPlayer.getUniqueId()) != null) {
                            Data.getShowcase(otherPlayer.getUniqueId()).openForPlayer(player);
                            // if they don't have a showcase, send an error message.
                        } else {
                            player.sendMessage(Utils.chatColor(Utils.prefix +
                                    Utils.placeholderPlayer(otherPlayer,
                                            plugin.getConfig().getString("messages.player-not-created-showcase"))));
                        }
                        return true;
                    }
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                    player.sendMessage(Utils.chatColor(Utils.prefix +
                            Utils.placeholderPlayer(p,
                                    plugin.getConfig().getString("messages.player-has-never-joined"))));
                    return true;

                    // Player doesn't exist.
                } catch (Exception e) {
                    player.sendMessage(Utils.chatColor(Utils.prefix +
                            plugin.getConfig().getString("messages.unknown-player")));
                }
                return true;
            }

            // Open player's own showcase.
            // Args length is 0, open the player's own showcase.
            PlayerShowcase showcase = Data.getShowcase(player.getUniqueId());
            // if the player already has a showcase, open it.
            if (showcase != null) {
                showcase.openForPlayer(player);
                // if the player does not already have a showcase, create one for them and open it.
            } else {
                Data.showcases.add(new PlayerShowcase(player.getUniqueId(), new ItemStack[0]));
            }
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
}
