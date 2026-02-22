package dev.naspo.showcase.commandstuff;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.datamanagement.DataManager;
import dev.naspo.showcase.services.OpenShowcaseService;
import dev.naspo.showcase.utils.PlayerUtils;
import dev.naspo.showcase.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    private Showcase plugin;
    private OpenShowcaseService openShowcase;

    public Commands(Showcase plugin, OpenShowcaseService openShowcase) {
        this.plugin = plugin;
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
        PlayerUtils.sendFormattedMessage(plugin, player, plugin.getConfig().getString("messages.reload"));
    }

    // Handles when a player runs the help command. Displays plugin commands available to them.
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
        // If they have the reload permission, also display the reload command.
        if (player.hasPermission("showcase.reload")) {
            player.sendMessage(Utils.chatColor(help[3]));
        }
    }

    // Try to find a player with the specified name, then call to open their showcase.
    private void handleOpenOtherShowcaseCommand(Player player, String targetPlayerName) {
        // If the player is online, open their showcase.
        if (PlayerUtils.isOnline(targetPlayerName)) {
            openShowcase.openOtherPlayerShowcase(player, PlayerUtils.getOnlinePlayer(targetPlayerName));
        } else {
            // Otherwise try for an offline player.
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayer(targetPlayerName);
            // If they have played before, open their showcase.
            if (offlinePlayer.hasPlayedBefore()) {
                openShowcase.openOtherPlayerShowcase(player, PlayerUtils.getOfflinePlayer(targetPlayerName));
            } else {
                // Otherwise, if they have never played the server before, send
                // a player-has-never-joined message.
                PlayerUtils.sendFormattedMessage(plugin, player,
                        plugin.getConfig().getString("messages.player-has-never-joined")
                        .replace("%player_name%", offlinePlayer.getName()));
            }
        }
    }

    // Utility method to send a no-permission message to the player.
    private void sendNoPermissionMessage(Player player) {
        PlayerUtils.sendFormattedMessage(plugin, player, plugin.getConfig().getString("messages.no-permission"));
    }
}
