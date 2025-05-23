package dev.naspo.showcase.listeners;

import dev.naspo.showcase.utils.Constants;
import dev.naspo.showcase.utils.Utils;
import org.bukkit.block.data.type.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChangeListener implements Listener {

    // Checks if "[Showcase]" is written on a sign. Creates a showcase sign if it is.
    @EventHandler
    private void onSignChange(SignChangeEvent event) {
        Sign sign = (Sign) event.getBlock();
        String[] lines = event.getLines();

        // Check if the sign contains "[Showcase]" (not case-sensitive).
        boolean hasShowcaseSignKeyword = false;
        for (String line : lines) {
            String trimmedLine = line.trim();

            if (!trimmedLine.isEmpty()) {
                // If the sign contains any text other than the showcase keyword, it does not qualify
                // for a showcase sign.
                if (!trimmedLine.equalsIgnoreCase(Constants.SHOWCASE_SIGN_KEYWORD)) {
                    return;
                } else {
                    hasShowcaseSignKeyword = true;
                }
            }
        }

        // If the sign is eligible to be a showcase sign, cancel the change event and make it one.
        if (hasShowcaseSignKeyword) {
            event.setCancelled(true);
            setShowcaseSignFor(event.getPlayer(), sign, event);
        }
    }

    // Sets a sign as a showcase sign for the specified player.
    // This is done by setting the sign's text as: "Player Name's Showcase" (in color).
    private void setShowcaseSignFor(Player player, Sign sign, SignChangeEvent event) {
        String[] lines = new String[4];
        String playerName = player.getName();

        // Build the showcase sign text.
        // If the player's name is longer than 13 characters, split their name across two lines.
        // (A sign can have up 15 characters per line, while a player name can have 16 characters. 12 is our max
        // because we also need to account for the "&" and "'s").
        if (playerName.length() > 12) {
            lines[0] = "&" + playerName.substring(0, 11) + "-";
            lines[1] = "&" + playerName.substring(11) + "'s";
            lines[2] = "&Showcase";
            lines[3] = "";
        } else {
            lines[0] = "";
            lines[1] = "&" + playerName + "'s";
            lines[2] = "&Showcase";
            lines[3] = "";
        }

        // Set the lines on the sign.
        for (int i = 0; i < 4; i++) {
            event.setLine(i, Utils.chatColor(lines[i]));
        }
    }
}
