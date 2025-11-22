package fr.maxlego08.zauctionhouse;

import fr.maxlego08.zauctionhouse.api.AuctionManager;
import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionRemoveFromListingEvent;
import fr.maxlego08.zauctionhouse.api.inventories.Inventories;
import fr.maxlego08.zauctionhouse.api.items.AuctionItem;
import fr.maxlego08.zauctionhouse.api.items.Item;
import fr.maxlego08.zauctionhouse.api.items.ItemStatus;
import fr.maxlego08.zauctionhouse.api.items.StorageType;
import fr.maxlego08.zauctionhouse.api.messages.Message;
import fr.maxlego08.zauctionhouse.api.services.AuctionPurchaseService;
import fr.maxlego08.zauctionhouse.api.services.AuctionRemoveService;
import fr.maxlego08.zauctionhouse.api.services.AuctionSellService;
import fr.maxlego08.zauctionhouse.services.PurchaseService;
import fr.maxlego08.zauctionhouse.services.RemoveService;
import fr.maxlego08.zauctionhouse.services.SellService;
import fr.maxlego08.zauctionhouse.utils.ZUtils;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZAuctionManager extends ZUtils implements AuctionManager {

    private final AuctionPlugin plugin;
    private final AuctionPurchaseService auctionPurchaseService;
    private final AuctionSellService auctionSellService;
    private final AuctionRemoveService auctionRemoveService;

    private final Map<StorageType, List<Item>> storageItems = new HashMap<>();

    public ZAuctionManager(AuctionPlugin plugin) {
        this.plugin = plugin;
        this.auctionPurchaseService = new PurchaseService(plugin);
        this.auctionSellService = new SellService(plugin, this);
        this.auctionRemoveService = new RemoveService(plugin);

        for (StorageType value : StorageType.values()) {
            this.storageItems.put(value, new ArrayList<>());
        }
    }

    @Override
    public void openMainAuction(Player player) {
        this.openMainAuction(player, 1);
    }

    @Override
    public void openMainAuction(Player player, int page) {
        var inventoriesLoader = this.plugin.getInventoriesLoader();
        inventoriesLoader.openInventory(player, Inventories.AUCTION, page);
    }

    public AuctionPlugin getPlugin() {
        return plugin;
    }

    @Override
    public AuctionPurchaseService getPurchaseService() {
        return auctionPurchaseService;
    }

    @Override
    public AuctionSellService getSellService() {
        return auctionSellService;
    }

    @Override
    public AuctionRemoveService getRemoveService() {
        return auctionRemoveService;
    }

    @Override
    public List<Item> getItems(StorageType storageType) {
        return this.storageItems.get(storageType);
    }

    @Override
    public void addItem(StorageType storageType, Item item) {
        getItems(storageType).add(item);
    }

    @Override
    public void removeItem(StorageType storageType, Item item) {
        getItems(storageType).remove(item);
    }

    @Override
    public void removeItem(StorageType storageType, int itemId) {
        getItems(storageType).removeIf(item -> item.getId() == itemId);
    }

    @Override
    public List<Item> getSortItems(Player player) {

        if (player.hasMetadata("auction-items")) {
            return (List<Item>) player.getMetadata("auction-items").getFirst().value();
        }

        var items = getItems(StorageType.LISTING);
        player.setMetadata("auction-items", new FixedMetadataValue(this.plugin, items));

        return items;
    }

    @Override
    public void removeItemFromListing(Player player, Item item) {

        var configuration = this.plugin.getConfiguration();
        var storageManager = this.plugin.getStorageManager();

        item.setStatus(ItemStatus.REMOVED);
        removeItem(StorageType.LISTING, item);

        if (configuration.getActions().giveItemAfterRemoveRemoveFromListing() && item.canReceiveItem(player)) {

            storageManager.updateItem(item, StorageType.DELETED);
            giveItem(player, item);

        } else {

            var expiration = configuration.getExpireExpiration().getExpiration(player);
            long expiredAt = expiration > 0 ? System.currentTimeMillis() + (expiration * 1000) : 0;
            item.setExpiredAt(new Date(expiredAt));

            addItem(StorageType.EXPIRED, item);
            storageManager.updateItem(item, StorageType.EXPIRED);
        }

        message(this.plugin, player, Message.ITEM_REMOVE_SUCCESS, "%amount%", item.getAmount(), "%item-translation-key%", item.getTranslationKey(), "%price%", item.getFormattedPrice());

        if (configuration.getActions().openInventoryAfterRemoveFromListing()) {
            openMainAuction(player);
        } else {
            player.closeInventory();
        }

        var event = new AuctionRemoveFromListingEvent(item, player);
        event.callEvent();

        // ToDo Logs
    }

    private void giveItem(Player player, Item item) {
        if (item instanceof AuctionItem auctionItem) {

            var itemStack = auctionItem.getItemStack();
            player.getInventory().addItem(itemStack);

        } else plugin.getLogger().severe("give item not implemented");
    }
}
