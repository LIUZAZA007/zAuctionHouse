package fr.maxlego08.zauctionhouse.services;

import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.cache.PlayerCacheKey;
import fr.maxlego08.zauctionhouse.api.event.events.remove.AuctionPreRemoveExpiredItemEvent;
import fr.maxlego08.zauctionhouse.api.event.events.remove.AuctionPreRemoveListedItemEvent;
import fr.maxlego08.zauctionhouse.api.event.events.remove.AuctionPreRemovePurchasedItemEvent;
import fr.maxlego08.zauctionhouse.api.item.Item;
import fr.maxlego08.zauctionhouse.api.item.ItemStatus;
import fr.maxlego08.zauctionhouse.api.item.StorageType;
import fr.maxlego08.zauctionhouse.api.services.AuctionRemoveService;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class RemoveService extends AuctionService implements AuctionRemoveService {

    private final AuctionPlugin plugin;

    public RemoveService(AuctionPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Void> removeListedItem(Player player, Item item) {

        var event = new AuctionPreRemoveListedItemEvent(item, player);
        if (!event.callEvent()) return CompletableFuture.completedFuture(null);

        var auctionManager = this.plugin.getAuctionManager();
        var inventoryManager = this.plugin.getInventoriesLoader().getInventoryManager();
        var logger = this.plugin.getLogger();

        // 1. Vérifier si l'item est expiré
        if (item.isExpired()) {
            logger.info("Item expired");
            auctionManager.getCache(player).remove(PlayerCacheKey.ITEMS_LISTED);
            auctionManager.openMainAuction(player);
            return CompletableFuture.completedFuture(null);
        }

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            logger.info("Item not available");
            auctionManager.openMainAuction(player);
            return CompletableFuture.completedFuture(null);
        }

        item.setStatus(ItemStatus.IS_BEING_REMOVED);

        // 2. Vérifier si l'item est lock
        return executeRemoval(player, item, () -> inventoryManager.updateInventory(player), () -> auctionManager.removeListedItem(player, item), StorageType.LISTED);
    }

    @Override
    public CompletableFuture<Void> removeOwnedItem(Player player, Item item) {


        var event = new AuctionPreRemoveListedItemEvent(item, player);
        if (!event.callEvent()) return CompletableFuture.completedFuture(null);

        var auctionManager = this.plugin.getAuctionManager();
        var inventoryManager = this.plugin.getInventoriesLoader().getInventoryManager();
        var logger = this.plugin.getLogger();

        // 1. Vérifier si l'item est expiré
        if (item.isExpired()) {
            logger.info("Item expired");
            auctionManager.getCache(player).remove(PlayerCacheKey.ITEMS_LISTED);
            auctionManager.openMainAuction(player);
            return CompletableFuture.completedFuture(null);
        }

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            logger.info("Item not available");
            auctionManager.openMainAuction(player);
            return CompletableFuture.completedFuture(null);
        }

        item.setStatus(ItemStatus.IS_BEING_REMOVED);

        // 2. Vérifier si l'item est lock
        return executeRemoval(player, item, () -> inventoryManager.updateInventory(player), () -> auctionManager.removeOwnedItem(player, item), StorageType.LISTED);

    }

    @Override
    public CompletableFuture<Void> removeExpiredItem(Player player, Item item) {

        var event = new AuctionPreRemoveExpiredItemEvent(item, player);
        if (!event.callEvent()) return CompletableFuture.completedFuture(null);

        var auctionManager = this.plugin.getAuctionManager();
        var inventoryManager = this.plugin.getInventoriesLoader().getInventoryManager();
        var logger = this.plugin.getLogger();

        // 1. Vérifier si l'item est expiré
        if (item.isExpired()) {
            logger.info("Item expired");
            auctionManager.getCache(player).remove(PlayerCacheKey.ITEMS_EXPIRED);
            inventoryManager.updateInventory(player);
            return CompletableFuture.completedFuture(null);
        }

        if (item.getStatus() != ItemStatus.REMOVED) {
            logger.info("Item not available");
            inventoryManager.updateInventory(player);
            return CompletableFuture.completedFuture(null);
        }

        item.setStatus(ItemStatus.DELETED);

        // 2. Vérifier si l'item est lock
        return executeRemoval(player, item, () -> inventoryManager.updateInventory(player), () -> this.plugin.getAuctionManager().removeExpiredItem(player, item), StorageType.EXPIRED);
    }

    @Override
    public CompletableFuture<Void> removePurchasedItem(Player player, Item item) {

        var event = new AuctionPreRemovePurchasedItemEvent(item, player);
        if (!event.callEvent()) return CompletableFuture.completedFuture(null);

        var auctionManager = this.plugin.getAuctionManager();
        var inventoryManager = this.plugin.getInventoriesLoader().getInventoryManager();
        var logger = this.plugin.getLogger();

        // 1. Vérifier si l'item est expiré
        if (item.isExpired()) {
            logger.info("Item expired");
            auctionManager.getCache(player).remove(PlayerCacheKey.ITEMS_EXPIRED);
            inventoryManager.updateInventory(player);
            return CompletableFuture.completedFuture(null);
        }

        if (item.getStatus() != ItemStatus.PURCHASED) {
            logger.info("Item not available");
            inventoryManager.updateInventory(player);
            return CompletableFuture.completedFuture(null);
        }

        item.setStatus(ItemStatus.DELETED);

        var manager = this.plugin.getAuctionManager();
        return executeRemoval(player, item, () -> manager.updateInventory(player), () -> manager.removePurchasedItem(player, item), StorageType.PURCHASED);

    }

    private CompletableFuture<Void> executeRemoval(Player player, Item item, Runnable onUnavailable, Supplier<CompletableFuture<Void>> onLocalRemoval, StorageType storageType) {

        var clusterBridge = this.plugin.getAuctionClusterBridge();
        var logger = this.plugin.getLogger();

        return clusterBridge.checkAvailability(item).thenCompose(available -> {

            if (!available) {
                logger.info("Item is not available");
                onUnavailable.run();
                return failedFuture(new IllegalStateException("Item introuvable"));
            }

            return clusterBridge.lockItem(item, player.getUniqueId());

        }).thenCompose(token -> onLocalRemoval.get()
                .thenCompose(v -> clusterBridge.removeItem(item, storageType)
                        .thenCompose(vv -> clusterBridge.unlockItem(item, token))))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}
