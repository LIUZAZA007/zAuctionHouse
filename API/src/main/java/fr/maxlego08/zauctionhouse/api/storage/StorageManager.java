package fr.maxlego08.zauctionhouse.api.storage;

import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import fr.maxlego08.zauctionhouse.api.items.AuctionItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageManager {

    boolean onEnable();

    void onDisable();

    void upsertPlayer(Player player);

    CompletableFuture<AuctionItem> createAuctionItem(Player seller, BigDecimal price, long expiredAt, ItemStack clonedItemStack, AuctionEconomy auctionEconomy);
}
