package fr.maxlego08.zauctionhouse.rule.rules;

import fr.maxlego08.zauctionhouse.api.rules.Rule;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;

/**
 * Rule that matches items whose material name contains one of the specified patterns.
 * Useful for matching groups like "BANNER", "WOOL", "CANDLE", etc.
 */
public class MaterialContainsRule implements Rule {

    private final List<String> patterns;

    public MaterialContainsRule(List<String> patterns) {
        this.patterns = patterns.stream()
                .map(s -> s.toUpperCase(Locale.ROOT))
                .toList();
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return false;

        String materialName = itemStack.getType().name();
        for (String pattern : patterns) {
            if (materialName.contains(pattern)) {
                return true;
            }
        }
        return false;
    }
}
