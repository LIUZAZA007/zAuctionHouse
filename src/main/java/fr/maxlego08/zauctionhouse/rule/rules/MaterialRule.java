package fr.maxlego08.zauctionhouse.rule.rules;

import fr.maxlego08.zauctionhouse.api.rules.Rule;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class MaterialRule implements Rule {

    private final Set<Material> materials;

    public MaterialRule(Set<Material> materials) {
        this.materials = materials;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return false;
        return materials.contains(itemStack.getType());
    }
}
