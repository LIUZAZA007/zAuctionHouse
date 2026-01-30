package fr.maxlego08.zauctionhouse.rule.rules;

import fr.maxlego08.zauctionhouse.api.rules.Rule;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;

/**
 * Rule that matches items whose material name ends with one of the specified suffixes.
 * Useful for matching groups like "_SWORD", "_PICKAXE", "_HELMET", etc.
 */
public class MaterialSuffixRule implements Rule {

    private final List<String> suffixes;

    public MaterialSuffixRule(List<String> suffixes) {
        this.suffixes = suffixes.stream()
                .map(s -> s.toUpperCase(Locale.ROOT))
                .toList();
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return false;

        String materialName = itemStack.getType().name();
        for (String suffix : suffixes) {
            if (materialName.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
