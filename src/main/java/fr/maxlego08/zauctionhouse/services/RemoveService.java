package fr.maxlego08.zauctionhouse.services;

import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.cluster.LockToken;
import fr.maxlego08.zauctionhouse.api.items.Item;
import fr.maxlego08.zauctionhouse.api.items.ItemStatus;
import fr.maxlego08.zauctionhouse.api.services.AuctionRemoveService;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class RemoveService implements AuctionRemoveService {

    private final AuctionPlugin plugin;

    public RemoveService(AuctionPlugin plugin) {
        this.plugin = plugin;
    }

    private static <T> CompletableFuture<T> failedFuture(Throwable ex) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(ex);
        return future;
    }

    @Override
    public void removeItemFromListing(Player player, Item item) {

        var inventoryManager = this.plugin.getInventoriesLoader().getInventoryManager();
        var clusterBridge = this.plugin.getAuctionClusterBridge();
        var logger = this.plugin.getLogger();

        // 1. Vérifier si l'item est expiré
        if (item.isExpired()) {
            logger.info("Item expired");
            inventoryManager.updateInventory(player);
            return;
        }

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            logger.info("Item not available");
            inventoryManager.updateInventory(player);
            return;
        }

        item.setStatus(ItemStatus.IS_BEING_REMOVED);

        // 2. Vérifier si l'item est lock
        clusterBridge.checkAvailability(item).thenCompose(available -> {

            if (!available) {
                logger.info("Item is not available");
                inventoryManager.updateInventory(player);
                item.setStatus(ItemStatus.AVAILABLE);
                return failedFuture(new IllegalStateException("Item introuvable"));
            }

            return clusterBridge.lockItem(item, player.getUniqueId());

        }).thenCompose(token -> {

            // 3. On va supprimer l'item coté REDIS

            logger.info("Token: " + token);
            return clusterBridge.removeItem(item);

        }).thenCompose(v -> {

            // 4. On supprime l'item en local
            this.plugin.getAuctionManager().removeItemFromListing(player, item);

            return clusterBridge.unlockItem(item, LockToken.of(item));
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }
}
