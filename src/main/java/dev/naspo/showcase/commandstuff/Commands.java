package dev.naspo.showcase.commandstuff;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.Utils;
import dev.naspo.showcase.datamanagement.Data;
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
        // Console command stuff.
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage("Did you mean /showcase reload?");
                return true;
            }
            // reload command.
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                sender.sendMessage("Showcase has been reloaded.");
            }
            return true;
        }

        // Player command stuff.
        // Capturing the player.
        Player player = (Player) sender;

        // Base permission check.
        if (!player.hasPermission("showcase.use") && !player.hasPermission("showcase.use.view")) {
            sendNoPermissionMessage(player);
            return true;
        }

        // If there are no args, have the player open their own showcase.
        if (args.length == 0) {
            openShowcase.openOwnShowcase(player);
        } else {
            // Handle arguments.
            switch (args[0].toLowerCase()) {
                case "reload" -> handleReloadCommand(player);
                case "help" -> handleHelpCommand(player);
                default -> handleOpenOtherShowcaseCommand(player, args[0]);
            };
        }
        return true;
    }

    // --- PLAYER INDIVIDUAL COMMAND LOGIC ---

    private void handleReloadCommand(Player player) {
        // Reload permission check.
        if (!(player.hasPermission("showcase.reload"))) {
            sendNoPermissionMessage(player);
            return;
        }

        // Reload the plugin.
        plugin.reloadConfig();
        player.sendMessage(Utils.chatColor(Utils.getPluginPrefix() +
                plugin.getConfig().getString("messages.reload")));
    }

    private void handleHelpCommand(Player player) {
        String[] help = {
                "&5&lShowcase Help",
                "&5/showcase &7- Open your showcase.",
                "&5/showcase <player> &7- View another player's showcase.",
                "&5/showcase reload &7- Reload the plugin."
        };
        for (int i = 0; i <= 2; i++) {
            player.sendMessage(Utils.chatColor(help[i]));
        }
        if (player.hasPermission("showcase.reload")) {
            player.sendMessage(Utils.chatColor(help[3]));
        }
    }

    // Try to find a player with the specified name, then call to open their showcase.
    private void handleOpenOtherShowcaseCommand(Player player, String targetPlayerName) {
        // Check if player is online.
        List<Player> onlinePlayers = new ArrayList<>();
        onlinePlayers.addAll(Bukkit.getOnlinePlayers());
        for (Player target : onlinePlayers) {
            if (targetPlayerName.equalsIgnoreCase(target.getName().toLowerCase())) {
                openShowcase.openOthersOnlineInv(player, target);
                return;
            }
        }

        // Check if target player is offline.
        try {
            // If they have played the server before, open their showcase.
            if (Bukkit.getOfflinePlayer(targetPlayerName).hasPlayedBefore()) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetPlayerName);
                openShowcase.openOthersOfflineInv(player, target);
                return;
            }

            // Otherwise, if they have never played the server before, send
            // a player-has-never-joined message.
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetPlayerName);
            player.sendMessage(Utils.chatColor(Utils.getPluginPrefix() +
                    Utils.placeholderPlayer(target,
                            plugin.getConfig().getString("messages.player-has-never-joined"))));

        // Player doesn't exist error.
        } catch (Exception e) {
            // Send a message to the command sender.
            player.sendMessage(Utils.chatColor(Utils.getPluginPrefix() +
                    plugin.getConfig().getString("messages.unknown-player")));
        }
    }

    // Utility method to send a no-permission message to the player.
    private void sendNoPermissionMessage(Player player) {
        player.sendMessage(Utils.chatColor(Utils.getPluginPrefix() +
                plugin.getConfig().getString("messages.no-permission")));
    }
}
