package fr.maxlego08.zauctionhouse.rule.rules;

import fr.maxlego08.zauctionhouse.api.rules.Rule;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

public class ModelDataRule implements Rule {

    private final Set<Integer> modelData;

    public ModelDataRule(Set<Integer> modelData) {
        this.modelData = modelData;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (itemStack == null) return false;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || !meta.hasCustomModelData()) return false;

        return modelData.contains(meta.getCustomModelData());
    }
}
