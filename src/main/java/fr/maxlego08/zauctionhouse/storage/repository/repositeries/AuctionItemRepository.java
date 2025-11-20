package fr.maxlego08.zauctionhouse.storage.repository.repositeries;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import fr.maxlego08.zauctionhouse.api.items.AuctionItem;
import fr.maxlego08.zauctionhouse.api.items.StorageType;
import fr.maxlego08.zauctionhouse.api.storage.Repository;
import fr.maxlego08.zauctionhouse.api.storage.Tables;
import fr.maxlego08.zauctionhouse.api.utils.Base64ItemStack;
import fr.maxlego08.zauctionhouse.items.ZAuctionItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Date;

public class AuctionItemRepository extends Repository {

    public AuctionItemRepository(AuctionPlugin plugin, DatabaseConnection connection) {
        super(plugin, connection, Tables.AUCTION_ITEMS);
    }

    public AuctionItem create(Player seller, BigDecimal price, long expiredAt, ItemStack clonedItemStack, AuctionEconomy auctionEconomy) {
        var expiredAtDate = new Date(expiredAt);
        var auctionId = insertSchema(schema -> {
            schema.uuid("seller_unique_id", seller.getUniqueId());
            schema.string("itemstack", Base64ItemStack.encode(clonedItemStack));
            schema.string("economy_name", auctionEconomy.getName());
            schema.decimal("price", price);
            schema.object("expired_at", expiredAtDate);
            schema.object("storage_type", StorageType.STORAGE);
        });
        return new ZAuctionItem(auctionId, seller.getUniqueId(), seller.getName(), price, auctionEconomy, new Date(), expiredAtDate, clonedItemStack);
    }
}
