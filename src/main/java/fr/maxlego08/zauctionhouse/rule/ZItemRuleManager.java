package fr.maxlego08.zauctionhouse.rule;

import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.event.events.RuleLoadEvent;
import fr.maxlego08.zauctionhouse.api.rules.ItemRuleManager;
import fr.maxlego08.zauctionhouse.api.rules.Rule;
import fr.maxlego08.zauctionhouse.api.rules.Rules;
import fr.maxlego08.zauctionhouse.rule.rules.AndRule;
import fr.maxlego08.zauctionhouse.rule.rules.LoreContainsRule;
import fr.maxlego08.zauctionhouse.rule.rules.MaterialRule;
import fr.maxlego08.zauctionhouse.rule.rules.ModelDataRule;
import fr.maxlego08.zauctionhouse.rule.rules.NameContainsRule;
import fr.maxlego08.zauctionhouse.rule.rules.TagRule;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ZItemRuleManager implements ItemRuleManager {

    private final AuctionPlugin plugin;
    private Rules blacklist = Rules.emptyDisabled();
    private Rules whitelist = Rules.emptyDisabled();

    public ZItemRuleManager(AuctionPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isBlacklisted(ItemStack itemStack) {
        return blacklist.matches(itemStack);
    }

    @Override
    public boolean isWhitelisted(ItemStack itemStack) {
        return whitelist.matches(itemStack);
    }

    @Override
    public boolean isAllowed(ItemStack itemStack) {

        if (whitelist.enabled() && whitelist.matches(itemStack)) {
            return true;
        }

        return !blacklist.enabled() || !blacklist.matches(itemStack);
    }

    @Override
    public Rules blacklistRules() {
        return blacklist;
    }

    @Override
    public Rules whitelistRules() {
        return whitelist;
    }

    @Override
    public boolean isBlacklistEnabled() {
        return blacklist.enabled();
    }

    @Override
    public void setBlacklistEnabled(boolean enabled) {
        this.blacklist = this.blacklist.withEnabled(enabled);
    }

    @Override
    public boolean isWhitelistEnabled() {
        return whitelist.enabled();
    }

    @Override
    public void setWhitelistEnabled(boolean enabled) {
        this.whitelist = this.whitelist.withEnabled(enabled);
    }

    @Override
    public void addBlacklistRule(Rule rule) {
        this.blacklist = this.blacklist.withAddedRule(rule);
    }

    @Override
    public void addWhitelistRule(Rule rule) {
        this.whitelist = this.whitelist.withAddedRule(rule);
    }

    @Override
    public void setBlacklistRules(Rules rules) {
        this.blacklist = rules;
    }

    @Override
    public void setWhitelistRules(Rules rules) {
        this.whitelist = rules;
    }

    @Override
    public void loadRules() {

        File file = new File(this.plugin.getDataFolder(), "rules.yml");
        if (!file.exists()) {
            this.plugin.saveFile("rules.yml", false);
        }

        RuleLoadEvent event = new RuleLoadEvent(this);
        event.callEvent();

        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        this.blacklist = loadRuleSet(configuration, "blacklist", false);
        this.whitelist = loadRuleSet(configuration, "whitelist", true);
    }

    private Rules loadRuleSet(FileConfiguration config, String basePath, boolean defaultEnabled) {
        boolean enabled = config.getBoolean(basePath + ".enabled", defaultEnabled);

        List<Map<?, ?>> maps = config.getMapList(basePath + ".rules");
        if (maps.isEmpty()) {
            return new Rules(enabled, List.of());
        }

        List<Rule> topLevelRules = new ArrayList<>();

        for (Map<?, ?> map : maps) {
            List<Rule> subRules = buildSubRulesFromMap(map);
            if (!subRules.isEmpty()) {
                topLevelRules.add(new AndRule(subRules));
            }
        }

        return new Rules(enabled, topLevelRules);
    }

    private List<Rule> buildSubRulesFromMap(Map<?, ?> map) {
        List<Rule> subRules = new ArrayList<>();

        // materials
        List<String> materialNames = asStringList(map.get("materials"));
        if (!materialNames.isEmpty()) {
            Set<Material> materials = materialNames.stream().map(name -> {
                try {
                    return Material.valueOf(name.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toSet());

            if (!materials.isEmpty()) {
                subRules.add(new MaterialRule(materials));
            }
        }

        List<String> nameContains = asStringList(map.get("name-contains"));
        if (!nameContains.isEmpty()) {
            subRules.add(new NameContainsRule(nameContains));
        }

        List<String> loreContains = asStringList(map.get("lore-contains"));
        if (!loreContains.isEmpty()) {
            subRules.add(new LoreContainsRule(loreContains));
        }

        List<Integer> modelData = asIntegerList(map.get("model-data"));
        if (!modelData.isEmpty()) {
            subRules.add(new ModelDataRule(new HashSet<>(modelData)));
        }

        List<String> tagNames = asStringList(map.get("tag-material"));
        if (!tagNames.isEmpty()) {
            subRules.add(new TagRule(tagNames));
        }

        return subRules;
    }

    private List<String> asStringList(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (Object o : list) {
            if (o != null) {
                result.add(String.valueOf(o));
            }
        }
        return result;
    }

    private List<Integer> asIntegerList(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        List<Integer> result = new ArrayList<>();
        for (Object o : list) {
            if (o instanceof Number number) {
                result.add(number.intValue());
            } else if (o != null) {
                try {
                    result.add(Integer.parseInt(String.valueOf(o)));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return result;
    }
}

