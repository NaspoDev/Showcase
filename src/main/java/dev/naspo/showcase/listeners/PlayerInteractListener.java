package dev.naspo.showcase.listeners;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.services.OpenShowcaseService;
import dev.naspo.showcase.utils.PlayerUtils;
import dev.naspo.showcase.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Optional;

public class PlayerInteractListener implements Listener {

    // A showcase sign will always end with this text.
    private final String SHOWCASE_SIGN_ENDING = "'s Showcase";
    private Showcase plugin;
    private OpenShowcaseService openShowcase;

    public PlayerInteractListener(Showcase plugin, OpenShowcaseService openShowcase) {
        this.plugin = plugin;
        this.openShowcase = openShowcase;
    }

    // Checks and handles when a showcase sign is right-clicked.
    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // If the action is left-clicking on a block...
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) event.getClickedBlock().getState();

                if (!isShowcaseSign(sign)) {
                    return;
                }

                // Because it's a Showcase sign, stop the player from editing the sign.
                event.setCancelled(true);

                // Get the target player's name from the showcase sign.
                String targetPlayerName = getPlayerNameFromShowcaseSign(sign);
                // If they are online, open their showcase.
                if (PlayerUtils.isOnline(targetPlayerName)) {
                    openShowcase.openOthersOnlineInv(player, PlayerUtils.getOnlinePlayer(targetPlayerName));
                } else {
                    // Otherwise if they are offline (and have played the server before), open their showcase.
                    OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayer(targetPlayerName);
                    if (offlinePlayer.hasPlayedBefore()) {
                        openShowcase.openOthersOfflineInv(player, offlinePlayer);
                    }
                }
            }
        }
    }

    // Returns true if a sign is a showcase sign. (Showcase sign's end with the value of
    // the SHOWCASE_SIGN_ENDING constant).
    private boolean isShowcaseSign(Sign sign) {
        String[] lines = sign.getSide(Side.FRONT).getLines();

        // Concatenate all lines into one string.
        String concatenatedLines = String.join("", lines);

        // If the sign ends with the SHOWCASE_SING_ENDING, we can assume it is a showcase sign.
        return concatenatedLines.endsWith(SHOWCASE_SIGN_ENDING);
    }

    private String getPlayerNameFromShowcaseSign(Sign sign) throws InvalidParameterException {
        if (!isShowcaseSign(sign)) {
            throw new InvalidParameterException("The passed in sign is not a Showcase sign!");
        }

        String[] lines = sign.getSide(Side.FRONT).getLines();
        // Concatenate all lines into one string.
        String concatenatedLines = String.join("", lines);

        // We can assume that text before the SHOWCASE_SIGN_ENDING is the player's name.
        String playerName = concatenatedLines.substring(0, concatenatedLines.indexOf(SHOWCASE_SIGN_ENDING));
        return playerName;
    }
}

