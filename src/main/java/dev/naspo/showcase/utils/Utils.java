package dev.naspo.showcase.utils;

import dev.naspo.showcase.Showcase;
import org.bukkit.ChatColor;

import java.util.concurrent.TimeUnit;

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

    public static boolean cooldownsFeatureIsEnabled(Showcase plugin) {
        return plugin.getConfig().getBoolean("cooldowns.enabled");
    }

    // Formats milliseconds into a human-readable duration format.
    // Example output: "5d 2h 20min 10s"
    public static String formatDuration(long timeRemainingMillis) {
        // Time values are extracted by converting the largest time value first, then getting the remainder by
        // converting that time value back to millis and subtracting it from the original timeRemainingMillis value.
        // Then repeat down for each time value in descending order.

        long days = TimeUnit.MILLISECONDS.toDays(timeRemainingMillis);
        timeRemainingMillis -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(timeRemainingMillis);
        timeRemainingMillis -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemainingMillis);
        timeRemainingMillis -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemainingMillis);

        // Build the string.
        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append("d ");
        }
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        if (seconds > 0) {
            sb.append(seconds).append("s");
        }

        return sb.toString().trim();
    }
}
