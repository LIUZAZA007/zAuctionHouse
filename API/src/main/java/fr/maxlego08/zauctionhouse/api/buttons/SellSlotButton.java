package fr.maxlego08.zauctionhouse.api.buttons;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Base button representing the slots where players can drop the items they want
 * to sell.
 */
public abstract class SellSlotButton extends Button {

    protected final Set<Integer> slots = new HashSet<>();

    @Override
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);
        this.slots.clear();
        this.slots.addAll(getSlots());
    }

    public Set<Integer> getSlots() {
        return this.slots;
    }
}
