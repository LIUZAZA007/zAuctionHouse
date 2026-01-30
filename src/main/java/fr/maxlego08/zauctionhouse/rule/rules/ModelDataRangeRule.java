package fr.maxlego08.zauctionhouse.rule.rules;

import fr.maxlego08.zauctionhouse.api.rules.Rule;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Rule that matches items whose custom model data falls within a specified range.
 * Useful for matching custom items from resource packs.
 */
public class ModelDataRangeRule implements Rule {

    private final int min;
    private final int max;

    public ModelDataRangeRule(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (itemStack == null) return false;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || !meta.hasCustomModelData()) return false;

        int modelData = meta.getCustomModelData();
        return modelData >= min && modelData <= max;
    }
}
