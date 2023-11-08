package dev.naspo.showcase.otherfeatures;

import dev.naspo.showcase.commandstuff.OpenShowcase;
import org.bukkit.event.Listener;

// When a player types [Showcase] into a sign, that sign can be used to view that player's showcase.
public class SignFeature implements Listener {
    private OpenShowcase openShowcase;
    public SignFeature(OpenShowcase openShowcase) {
        this.openShowcase = openShowcase;
    }


}
