package fr.maxlego08.zauctionhouse.rule.rules;

import fr.maxlego08.zauctionhouse.api.rules.Rule;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Locale;

/**
 * Rule that matches items that have a lore line exactly equal to one of the specified values.
 * Comparison is case-insensitive and strips color codes.
 */
public class LoreEqualsRule implements Rule {

    private final List<String> loreLines;

    public LoreEqualsRule(List<String> loreLines) {
        this.loreLines = loreLines.stream()
                .map(ChatColor::stripColor)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .toList();
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (itemStack == null) return false;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || !meta.hasLore()) return false;

        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return false;

        for (String line : lore) {
            String cleanLine = ChatColor.stripColor(line).toLowerCase(Locale.ROOT);
            if (loreLines.contains(cleanLine)) {
                return true;
            }
        }
        return false;
    }
}
