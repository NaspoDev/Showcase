package dev.naspo.showcase.commandstuff;

import dev.naspo.showcase.datamanagement.Data;
import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor {

    private Showcase plugin;
    private Data data;
    private OpenShowcase openShowcase;
    public Commands(Showcase plugin, Data data, OpenShowcase openShowcase) {
        this.plugin = plugin;
        this.data = data;
        this.openShowcase = openShowcase;
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
                    plugin.reloadConfig();
                    sender.sendMessage("Showcase has been reloaded.");
                }
                return true;
            }

            // --- PLAYER COMMAND STUFF ---

            Player player = (Player) sender;

            // Base permission check.
            if (!player.hasPermission("showcase.use") && !player.hasPermission("showcase.use.view")) {
                player.sendMessage(Utils.chatColor(Utils.getPluginPrefix() +
                        plugin.getConfig().getString("messages.no-permission")));
                return true;
            }

            // If the command is written with arguments.
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

                // Check if player is online.
                List<Player> onlinePlayers = new ArrayList<>();
                onlinePlayers.addAll(Bukkit.getOnlinePlayers());
                for (Player p : onlinePlayers) {
                    if (args[0].equalsIgnoreCase(p.getName().toLowerCase())) {
                        openShowcase.openOthersOnlineInv(player, p);
                        return true;
                    }
                }

                // Check if player is offline.
                try {
                    // If they have played the server before, open their showcase.
                    if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                        OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                        openShowcase.openOthersOfflineInv(player, p);
                        return true;
                    }

                    // Otherwise, if they have never played the server before, send
                    // a player-has-never-joined message.
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                    player.sendMessage(Utils.chatColor(Utils.getPluginPrefix() +
                            Utils.placeholderPlayer(p,
                                    plugin.getConfig().getString("messages.player-has-never-joined"))));
                    return true;

                    // Player doesn't exist error.
                } catch (Exception e) {
                    // Send a message to the command sender.
                    player.sendMessage(Utils.chatColor(Utils.getPluginPrefix() +
                            plugin.getConfig().getString("messages.unknown-player")));
                }
                return true;
            }

            // No argument command.
            // Open the player's own showcase.
            openShowcase.openOwnShowcase(player);
        }
        return false;
    }

    // --- PLAYER INDIVIDUAL COMMAND LOGIC ---

    private void reloadCommand(Player player) {
        // Reload permission check.
        if (!(player.hasPermission("showcase.reload"))) {

            player.sendMessage(Utils.chatColor(Utils.getPluginPrefix() +
                    plugin.getConfig().getString("messages.no-permission")));
            return;
        }

        // Reload the plugin.
        plugin.reloadConfig();
        player.sendMessage(Utils.chatColor(Utils.getPluginPrefix() +
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
