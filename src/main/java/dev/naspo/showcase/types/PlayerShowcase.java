package dev.naspo.showcase.types;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

// A player showcase.
public class PlayerShowcase {
    private ItemStack[] items;
    private HashMap<Integer, Long> slotCooldowns;
    private final UUID ownerUUID;

    public PlayerShowcase(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        items = new ItemStack[0];
        slotCooldowns = new HashMap<>();
    }

    // Returns true if the specified slot is on cooldown.
    public boolean isSlotOnCooldown(int slot) {
        return slotCooldowns.get(slot) > System.currentTimeMillis();
    }

    // Getters and Setters

    public ItemStack[] getItems() {
        return items;
    }

    public void setItems(ItemStack[] items) {
        this.items = items;
    }

    public HashMap<Integer, Long> getSlotCooldowns() {
        return slotCooldowns;
    }

    public void setSlotCooldowns(HashMap<Integer, Long> slotCooldowns) {
        this.slotCooldowns = slotCooldowns;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }
}
