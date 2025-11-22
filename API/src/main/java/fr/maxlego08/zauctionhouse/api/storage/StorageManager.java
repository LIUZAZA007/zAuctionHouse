package fr.maxlego08.zauctionhouse.api.storage;

import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import fr.maxlego08.zauctionhouse.api.items.AuctionItem;
import fr.maxlego08.zauctionhouse.api.items.Item;
import fr.maxlego08.zauctionhouse.api.items.StorageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public interface StorageManager {

    boolean onEnable();

    void onDisable();

    void loadItems();

    void upsertPlayer(Player player);

    CompletableFuture<AuctionItem> createAuctionItem(Player seller, BigDecimal price, long expiredAt, ItemStack clonedItemStack, AuctionEconomy auctionEconomy);

    <T extends Repository> T with(Class<T> module);

    void updateItem(Item item, StorageType storageType);
}
