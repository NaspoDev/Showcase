package dev.naspo.showcase;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Utils {

    private static Showcase plugin;
    Utils(Showcase plugin) {
        Utils.plugin = plugin;
    }

    // Returns the plugins prefix.
    public static String getPluginPrefix() {
        return plugin.getConfig().getString("messages.prefix");
    }

    public static String chatColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    // Removes filetype extension from file name.
    public static String removeExtension(String name) {
        return name.substring(0, name.lastIndexOf('.'));
    }

    // --- PlaceholderAPI Stuff ---

    // Player expansion, for online players.
    public static String placeholderPlayer(Player player, String text) {
        return text = PlaceholderAPI.setPlaceholders(player, text);
    }

    // Player expansion, for offline players.
    public static String placeholderPlayer(OfflinePlayer player, String text) {
        return text = PlaceholderAPI.setPlaceholders(player, text);
    }
}
