package dev.naspo.showcase.utils;

import dev.naspo.showcase.Showcase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

// Player related utility methods.
public class PlayerUtils {

    // Get online player by UUID.
    public static Player getOnlinePlayer(UUID playerUUID) {
        return isOnline(playerUUID) ? Bukkit.getPlayer(playerUUID) : null;
    }

    // Get online player by name.
    public static Player getOnlinePlayer(String playerName) {
        return isOnline(playerName) ? Bukkit.getPlayer(playerName) : null;
    }

    // Get offline player by UUID.
    public static OfflinePlayer getOfflinePlayer(UUID playerUUID) {
        return Bukkit.getOfflinePlayer(playerUUID);
    }

    // Get offline player by name.
    public static OfflinePlayer getOfflinePlayer(String playerName) {
        return Bukkit.getOfflinePlayer(playerName);
    }

    // Returns true if the player is online.
    public static boolean isOnline(UUID playerUUID) {
        Optional<Player> player = Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> p.getUniqueId().equals(playerUUID))
                .findFirst()
                .map(p -> (Player) p);

        return player.isPresent();
    }

    // Returns true if the player is online.
    public static boolean isOnline(String playerName) {
        Optional<Player> player = Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> p.getName().equalsIgnoreCase(playerName))
                .findFirst()
                .map(p -> (Player) p);

        return player.isPresent();
    }

    /**
     * Sends a player a plugin-formatted message which includes the plugin's prefix and color code translation.
     * @param plugin The Showcase plugin.
     * @param player The player to send the message to.
     * @param message The message to send.
     */
    public static void sendFormattedMessage(Showcase plugin, Player player, String message) {
        player.sendMessage(
                 Utils.chatColor(Utils.getPluginPrefix(plugin) + message)
        );
    }
}
