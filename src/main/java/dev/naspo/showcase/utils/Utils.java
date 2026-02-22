package dev.naspo.showcase.utils;

import dev.naspo.showcase.Showcase;
import org.bukkit.ChatColor;

// General plugin utils.
public class Utils {

    // Returns the plugins prefix.
    public static String getPluginPrefix(Showcase plugin) {
        return plugin.getConfig().getString("messages.prefix");
    }

    public static String chatColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    // Removes filetype extension from file name.
    public static String removeFileExtension(String name) {
        return name.substring(0, name.lastIndexOf('.'));
    }
}
