package fr.maxlego08.zauctionhouse.inventory;

import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import fr.maxlego08.zauctionhouse.api.messages.Message;
import fr.maxlego08.zauctionhouse.utils.ZUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class SellInventoryLayout extends ZUtils {

    private final AuctionPlugin plugin;
    private LayoutData layoutData;

    public SellInventoryLayout(AuctionPlugin plugin) {
        this.plugin = plugin;
    }

    public SellInventoryHolder createHolder(Player player, BigDecimal price, long expiredAt, AuctionEconomy auctionEconomy) {
        LayoutData data = this.loadLayout();
        return new SellInventoryHolder(player.getUniqueId(), price, expiredAt, auctionEconomy, new HashSet<>(data.lockedSlots), data.confirmSlot, data.cancelSlot);
    }

    public Inventory buildInventory(SellInventoryHolder holder, BigDecimal price, AuctionEconomy auctionEconomy) {
        LayoutData data = this.loadLayout();

        String title = data.title != null ? data.title : this.getMessage(Message.SELL_INVENTORY_TITLE);
        Inventory inventory = this.plugin.getServer().createInventory(holder, data.size, ChatColor.translateAlternateColorCodes('&', title));

        var economyManager = this.plugin.getEconomyManager();
        String formattedPrice = economyManager.format(auctionEconomy, price);

        for (ItemConfig itemConfig : data.items.values()) {
            ItemStack itemStack = itemConfig.buildItemStack(formattedPrice, auctionEconomy.getName());
            for (int slot : itemConfig.slots) {
                if (slot >= 0 && slot < inventory.getSize()) {
                    inventory.setItem(slot, itemStack);
                }
            }
        }

        return inventory;
    }

    private LayoutData loadLayout() {
        File file = new File(this.plugin.getDataFolder(), "inventories/sell-inventory.yml");
        if (!file.exists()) {
            this.plugin.saveFile("inventories/sell-inventory.yml", false);
        }

        if (this.layoutData != null && this.layoutData.lastModified == file.lastModified()) {
            return this.layoutData;
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        LayoutData data = new LayoutData();
        data.lastModified = file.lastModified();
        data.title = configuration.getString("name", this.getMessage(Message.SELL_INVENTORY_TITLE));

        int size = configuration.getInt("size", 54);
        data.size = Math.min(54, Math.max(9, (int) (Math.ceil(size / 9.0) * 9)));

        Set<Integer> lockedSlots = new HashSet<>(this.readSlots(configuration.getList("locked-slots")));

        ConfigurationSection itemsSection = configuration.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection == null) continue;

                Set<Integer> slots = this.readSlots(itemSection.getList("slots"));
                ItemConfig itemConfig = this.readItemConfig(itemSection.getConfigurationSection("item"), slots);
                data.items.put(key.toLowerCase(Locale.ROOT), itemConfig);
                if (itemSection.getBoolean("locked", true)) {
                    lockedSlots.addAll(slots);
                }
            }
        }

        data.confirmSlot = data.items.getOrDefault("confirm", ItemConfig.empty(Set.of(SellInventoryHolder.CONFIRM_SLOT))).slots.stream().findFirst().orElse(SellInventoryHolder.CONFIRM_SLOT);
        data.cancelSlot = data.items.getOrDefault("cancel", ItemConfig.empty(Set.of(SellInventoryHolder.CANCEL_SLOT))).slots.stream().findFirst().orElse(SellInventoryHolder.CANCEL_SLOT);
        data.lockedSlots = lockedSlots;
        data.lockedSlots.add(data.confirmSlot);
        data.lockedSlots.add(data.cancelSlot);

        this.layoutData = data;
        return data;
    }

    private ItemConfig readItemConfig(ConfigurationSection section, Set<Integer> slots) {
        if (section == null) {
            return ItemConfig.empty(slots);
        }

        String materialName = section.getString("material", Material.GRAY_STAINED_GLASS_PANE.name());
        Material material = Material.matchMaterial(materialName);
        if (material == null) material = Material.GRAY_STAINED_GLASS_PANE;

        String name = section.getString("name", " ");
        List<String> lore = section.getStringList("lore");

        return new ItemConfig(material, name, lore, slots);
    }

    private Set<Integer> readSlots(List<?> slotObjects) {
        if (slotObjects == null) return Collections.emptySet();

        Set<Integer> slots = new HashSet<>();
        for (Object object : slotObjects) {
            if (object instanceof Number number) {
                slots.add(number.intValue());
                continue;
            }

            if (!(object instanceof String rawSlot)) continue;

            if (rawSlot.contains("-")) {
                String[] parts = rawSlot.split("-");
                if (parts.length != 2) continue;
                try {
                    int start = Integer.parseInt(parts[0]);
                    int end = Integer.parseInt(parts[1]);
                    for (int slot = Math.min(start, end); slot <= Math.max(start, end); slot++) {
                        slots.add(slot);
                    }
                } catch (NumberFormatException ignored) {
                }
                continue;
            }

            try {
                slots.add(Integer.parseInt(rawSlot));
            } catch (NumberFormatException ignored) {
            }
        }
        return slots;
    }

    private static class LayoutData {
        private long lastModified;
        private String title;
        private int size;
        private final Map<String, ItemConfig> items = new HashMap<>();
        private Set<Integer> lockedSlots = new HashSet<>();
        private int confirmSlot = SellInventoryHolder.CONFIRM_SLOT;
        private int cancelSlot = SellInventoryHolder.CANCEL_SLOT;
    }

    private static class ItemConfig {
        private final Material material;
        private final String name;
        private final List<String> lore;
        private final Set<Integer> slots;

        private ItemConfig(Material material, String name, List<String> lore, Set<Integer> slots) {
            this.material = material;
            this.name = name;
            this.lore = lore;
            this.slots = slots;
        }

        public static ItemConfig empty(Set<Integer> slots) {
            return new ItemConfig(Material.AIR, "", Collections.emptyList(), slots);
        }

        public ItemStack buildItemStack(String price, String economyName) {
            ItemStack itemStack = new ItemStack(this.material);
            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) return itemStack;

            String displayName = this.name
                    .replace("%price%", price)
                    .replace("%economy%", economyName);
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

            List<String> formattedLore = this.lore.stream()
                    .map(line -> line
                            .replace("%price%", price)
                            .replace("%economy%", economyName))
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());
            meta.setLore(formattedLore);

            itemStack.setItemMeta(meta);
            return itemStack;
        }
    }
}
