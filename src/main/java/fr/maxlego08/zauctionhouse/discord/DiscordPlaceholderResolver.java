package fr.maxlego08.zauctionhouse.discord;

import fr.maxlego08.zauctionhouse.api.category.Category;
import fr.maxlego08.zauctionhouse.api.item.items.AuctionItem;
import fr.maxlego08.zauctionhouse.utils.component.ComponentMessageHelper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DiscordPlaceholderResolver {

    private final Map<String, Supplier<String>> placeholders = new HashMap<>();

    public DiscordPlaceholderResolver(String serverName, AuctionItem auctionItem, Player seller, Player buyer) {
        registerItemPlaceholders(auctionItem);
        registerSellerPlaceholders(auctionItem, seller);
        registerBuyerPlaceholders(auctionItem, buyer);
        registerPricePlaceholders(auctionItem);
        registerTimePlaceholders(auctionItem);
        registerServerPlaceholders(serverName);
        registerCategoryPlaceholders(auctionItem);
    }

    private void registerItemPlaceholders(AuctionItem auctionItem) {
        placeholders.put("%item_id%", () -> String.valueOf(auctionItem.getId()));

        ItemStack itemStack = auctionItem.getItemStack();
        if (itemStack != null) {
            placeholders.put("%item_material%", () -> itemStack.getType().name());
            placeholders.put("%item_amount%", () -> String.valueOf(auctionItem.getAmount()));
            placeholders.put("%item_display%", () -> ComponentMessageHelper.componentMessage.stripColor(auctionItem.getItemDisplay()));

            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                placeholders.put("%item_lore%", () -> {
                    var lore = ComponentMessageHelper.componentMessage.getItemStackLore(itemStack);
                    return lore != null ? String.join("\n", lore) : "";
                });

                placeholders.put("%item_enchantments%", () -> {
                    if (meta.hasEnchants()) {
                        return meta.getEnchants().entrySet().stream().map(e -> formatEnchantment(e.getKey()) + " " + e.getValue()).collect(Collectors.joining(", "));
                    }
                    return "None";
                });

                placeholders.put("%item_custom_model_data%", () -> {
                    if (meta.hasCustomModelData()) {
                        return String.valueOf(meta.getCustomModelData());
                    }
                    return "0";
                });
            } else {
                placeholders.put("%item_lore%", () -> "");
                placeholders.put("%item_enchantments%", () -> "None");
                placeholders.put("%item_custom_model_data%", () -> "0");
            }
        } else {
            placeholders.put("%item_material%", () -> "UNKNOWN");
            placeholders.put("%item_amount%", () -> "0");
            placeholders.put("%item_display%", () -> "Unknown Item");
            placeholders.put("%item_lore%", () -> "");
            placeholders.put("%item_enchantments%", () -> "None");
            placeholders.put("%item_custom_model_data%", () -> "0");
        }
    }

    private void registerSellerPlaceholders(AuctionItem auctionItem, Player seller) {
        if (seller != null) {
            placeholders.put("%seller_name%", seller::getName);
            placeholders.put("%seller_uuid%", () -> seller.getUniqueId().toString());
        } else {
            placeholders.put("%seller_name%", auctionItem::getSellerName);
            placeholders.put("%seller_uuid%", () -> {
                UUID uuid = auctionItem.getSellerUniqueId();
                return uuid != null ? uuid.toString() : "";
            });
        }
    }

    private void registerBuyerPlaceholders(AuctionItem auctionItem, Player buyer) {
        if (buyer != null) {
            placeholders.put("%buyer_name%", buyer::getName);
            placeholders.put("%buyer_uuid%", () -> buyer.getUniqueId().toString());
        } else {
            String buyerName = auctionItem.getBuyerName();
            UUID buyerUuid = auctionItem.getBuyerUniqueId();
            placeholders.put("%buyer_name%", () -> buyerName != null ? buyerName : "Unknown");
            placeholders.put("%buyer_uuid%", () -> buyerUuid != null ? buyerUuid.toString() : "");
        }
    }

    private void registerPricePlaceholders(AuctionItem auctionItem) {
        placeholders.put("%price%", () -> auctionItem.getPrice().toPlainString());
        placeholders.put("%formatted_price%", auctionItem::getFormattedPrice);

        var economy = auctionItem.getAuctionEconomy();
        if (economy != null) {
            placeholders.put("%economy_name%", economy::getName);
            placeholders.put("%economy_display_name%", economy::getDisplayName);
        } else {
            placeholders.put("%economy_name%", () -> "default");
            placeholders.put("%economy_display_name%", () -> "Default");
        }
    }

    private void registerTimePlaceholders(AuctionItem auctionItem) {
        placeholders.put("%created_at%", () -> {
            var createdAt = auctionItem.getCreatedAt();
            return createdAt != null ? createdAt.toString() : "";
        });

        placeholders.put("%expires_at%", auctionItem::getFormattedExpireDate);
        placeholders.put("%remaining_time%", auctionItem::getRemainingTime);
        placeholders.put("%timestamp%", () -> Instant.now().toString());
    }

    private void registerServerPlaceholders(String serverName) {
        placeholders.put("%server_name%", () -> serverName);
    }

    private void registerCategoryPlaceholders(AuctionItem auctionItem) {
        placeholders.put("%category_names%", () -> {
            var categories = auctionItem.getCategories();
            if (categories == null || categories.isEmpty()) {
                return "None";
            }
            return categories.stream().map(Category::getDisplayName).collect(Collectors.joining(", "));
        });

        placeholders.put("%category_count%", () -> {
            var categories = auctionItem.getCategories();
            return String.valueOf(categories != null ? categories.size() : 0);
        });
    }

    public String resolve(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String result = text;
        for (Map.Entry<String, Supplier<String>> entry : placeholders.entrySet()) {
            if (result.contains(entry.getKey())) {
                String value = entry.getValue().get();
                result = result.replace(entry.getKey(), value != null ? value : "");
            }
        }

        return result;
    }

    private String formatEnchantment(Enchantment enchantment) {
        String key = enchantment.getKey().getKey();
        String[] parts = key.split("_");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                formatted.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase()).append(" ");
            }
        }
        return formatted.toString().trim();
    }
}
