package fr.maxlego08.zauctionhouse.api.rules;

import org.bukkit.inventory.ItemStack;

public interface Rule {
    boolean matches(ItemStack itemStack);
}
