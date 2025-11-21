package fr.maxlego08.zauctionhouse.api.configuration.records;

import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public record ItemLoreConfiguration(
        List<String> auctionItemLore,
        String sellerStatus,
        String buyerStatus
) {
    public static ItemLoreConfiguration of(AuctionPlugin plugin, FileConfiguration config) {
        return new ItemLoreConfiguration(
                config.getStringList("item-lore.auction-item"),
                config.getString("item-lore.seller-status"),
                config.getString("item-lore.buyer-status")
        );
    }
}
