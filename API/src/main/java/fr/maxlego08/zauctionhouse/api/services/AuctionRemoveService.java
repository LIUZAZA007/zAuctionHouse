package fr.maxlego08.zauctionhouse.api.services;

import fr.maxlego08.zauctionhouse.api.item.Item;
import org.bukkit.entity.Player;

public interface AuctionRemoveService {

    java.util.concurrent.CompletableFuture<Void> removeListedItem(Player player, Item item);

    java.util.concurrent.CompletableFuture<Void> removeOwnedItem(Player player, Item item);

    java.util.concurrent.CompletableFuture<Void> removeExpiredItem(Player player, Item item);

    java.util.concurrent.CompletableFuture<Void> removePurchasedItem(Player player, Item item);

}
