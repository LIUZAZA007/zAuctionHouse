package fr.maxlego08.zauctionhouse.api.rules;

import org.bukkit.inventory.ItemStack;

public interface ItemRuleManager {

    boolean isBlacklisted(ItemStack itemStack);

    boolean isWhitelisted(ItemStack itemStack);

    boolean isAllowed(ItemStack itemStack);

    Rules blacklistRules();

    Rules whitelistRules();

    boolean isBlacklistEnabled();

    void setBlacklistEnabled(boolean enabled);

    boolean isWhitelistEnabled();

    void setWhitelistEnabled(boolean enabled);

    void addBlacklistRule(Rule rule);

    void addWhitelistRule(Rule rule);

    void setBlacklistRules(Rules rules);

    void setWhitelistRules(Rules rules);

    void loadRules();
}
