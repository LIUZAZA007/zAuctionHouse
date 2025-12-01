package fr.maxlego08.zauctionhouse.rule.rules;

import fr.maxlego08.zauctionhouse.api.rules.Rule;
import fr.maxlego08.zauctionhouse.utils.component.ComponentMessageHelper;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LoreContainsRule implements Rule {

    private final List<String> needles;

    public LoreContainsRule(List<String> needles) {
        this.needles = needles.stream().map(s -> s.toLowerCase(Locale.ROOT)).toList();
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (itemStack == null) return false;

        var componentMessage = ComponentMessageHelper.componentMessage;
        var lore = componentMessage.getItemStackLore(itemStack);

        String joined = lore.stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.joining(" "));

        return needles.stream().anyMatch(joined::contains);
    }
}
