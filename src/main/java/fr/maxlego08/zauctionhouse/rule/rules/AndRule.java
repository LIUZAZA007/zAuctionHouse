package fr.maxlego08.zauctionhouse.rule.rules;

import fr.maxlego08.zauctionhouse.api.rules.Rule;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AndRule implements Rule {

    private final List<Rule> children;

    public AndRule(List<Rule> children) {
        this.children = children;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        for (Rule child : children) {
            if (!child.matches(itemStack)) {
                return false;
            }
        }
        return !children.isEmpty();
    }
}
