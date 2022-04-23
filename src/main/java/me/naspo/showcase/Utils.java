package me.naspo.showcase;

import org.bukkit.ChatColor;

public class Utils {
    public static String prefix;

    private static Showcase plugin;
    Utils(Showcase plugin) {
        Utils.plugin = plugin;

        reloadVars();
    }

    public static void reloadConfigs() {
        plugin.reloadConfig();

        reloadVars();
    }

    private static void reloadVars() {
        prefix = plugin.getConfig().getString("messages.prefix");
    }

    public static String chatColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    //Removes filetype extension from file name.
    public static String removeExtension(String name) {
        return name.substring(0, name.lastIndexOf('.'));
    }
}
