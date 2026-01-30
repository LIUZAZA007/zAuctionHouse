package fr.maxlego08.zauctionhouse.rule.rules;

import fr.maxlego08.zauctionhouse.api.rules.Rule;
import fr.maxlego08.zauctionhouse.utils.component.ComponentMessageHelper;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Locale;

/**
 * Rule that matches items whose display name exactly equals one of the specified values.
 * Supports case-insensitive matching and color code stripping.
 */
public class NameEqualsRule implements Rule {

    private final List<String> names;
    private final boolean ignoreCase;
    private final boolean stripColors;

    public NameEqualsRule(List<String> names, boolean ignoreCase, boolean stripColors) {
        this.ignoreCase = ignoreCase;
        this.stripColors = stripColors;

        if (ignoreCase) {
            this.names = names.stream()
                    .map(s -> stripColors ? ChatColor.stripColor(s) : s)
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .toList();
        } else {
            this.names = names.stream()
                    .map(s -> stripColors ? ChatColor.stripColor(s) : s)
                    .toList();
        }
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (itemStack == null) return false;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;

        String displayName = ComponentMessageHelper.componentMessage.getItemStackName(itemStack);
        if (stripColors) {
            displayName = ChatColor.stripColor(displayName);
        }
        if (ignoreCase) {
            displayName = displayName.toLowerCase(Locale.ROOT);
        }

        return names.contains(displayName);
    }
}
