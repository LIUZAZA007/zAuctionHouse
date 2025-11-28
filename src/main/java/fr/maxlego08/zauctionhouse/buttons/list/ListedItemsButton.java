package fr.maxlego08.zauctionhouse.buttons.list;

import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.cache.PlayerCacheKey;
import fr.maxlego08.zauctionhouse.api.inventories.Inventories;
import fr.maxlego08.zauctionhouse.api.item.Item;
import fr.maxlego08.zauctionhouse.api.messages.Message;
import fr.maxlego08.zauctionhouse.api.utils.Permission;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.logging.Level;

public class ListedItemsButton extends PaginateButton {

    private final AuctionPlugin plugin;

    public ListedItemsButton(Plugin plugin) {
        this.plugin = (AuctionPlugin) plugin;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventoryEngine) {

        var manager = this.plugin.getAuctionManager();
        var items = manager.getItemsListedForSale(player);

        paginate(items, inventoryEngine, (slot, item) -> {
            var itemStack = item.buildItemStack(player);
            inventoryEngine.addItem(slot, itemStack).setClick(event -> {

                if ((event.getClick() == ClickType.DROP || event.getClick() == ClickType.MIDDLE) && player.hasPermission(Permission.ZAUCTIONHOUSE_ADMIN_REMOVE_INVENTORY.asPermission())) {

                    // ToDo

                    return;
                }

                if (item.getSellerUniqueId().equals(player.getUniqueId())) {

                    // Remove item
                    if (this.plugin.getConfiguration().getActions().listed().openConfirmInventory()) {
                        var cache = manager.getCache(player);
                        cache.set(PlayerCacheKey.ITEM_SHOW, item);
                        cache.set(PlayerCacheKey.CURRENT_PAGE, this.plugin.getInventoriesLoader().getInventoryManager().getPage(player));

                        this.plugin.getInventoriesLoader().openInventory(player, Inventories.REMOVE_CONFIRM);
                    } else {
                        manager.getRemoveService().removeListedItem(player, item);
                    }
                } else {

                    // Purchase items
                    processPurchase(player, inventoryEngine, slot, item, itemStack);
                }
            });
        });
    }

    @Override
    public int getPaginationSize(Player player) {
        return this.plugin.getAuctionManager().getItemsListedForSale(player).size();
    }

    private void processPurchase(Player player, InventoryEngine inventoryEngine, int slot, Item item, ItemStack itemStack) {

        var configuration = this.plugin.getConfiguration();
        var actionConfiguration = configuration.getActions().purchased();
        var manager = this.plugin.getAuctionManager();
        var cache = manager.getCache(player);

        if (cache.get(PlayerCacheKey.PURCHASE_ITEM)) return;

        cache.set(PlayerCacheKey.PURCHASE_ITEM, true);

        var economy = item.getAuctionEconomy();
        economy.has(player, item.getPrice()).whenComplete((hasMoney, throwable) -> {

            if (throwable != null) {
                this.plugin.getLogger().log(Level.WARNING, "Cannot verify the balance of " + player.getName(), throwable);
                cache.set(PlayerCacheKey.PURCHASE_ITEM, false);
                return;
            }

            if (!hasMoney) {

                if (actionConfiguration.sendNoMoneyMessage()) {
                    manager.message(player, Message.NOT_ENOUGH_MONEY);
                }

                if (actionConfiguration.noMoney().enable()) {
                    var spigotInventory = inventoryEngine.getSpigotInventory();
                    spigotInventory.setItem(slot, actionConfiguration.noMoney().menuItemStack().build(player));
                    this.plugin.getScheduler().runLater(() -> {

                        if (inventoryEngine.isClose()) return;
                        spigotInventory.setItem(slot, itemStack);

                    }, actionConfiguration.noMoney().duration());
                }

                cache.set(PlayerCacheKey.PURCHASE_ITEM, false);
                return;
            }

            cache.set(PlayerCacheKey.ITEM_SHOW, item);
            cache.set(PlayerCacheKey.CURRENT_PAGE, this.plugin.getInventoriesLoader().getInventoryManager().getPage(player));
            cache.set(PlayerCacheKey.PURCHASE_ITEM, false);

            this.plugin.getInventoriesLoader().openInventory(player, Inventories.PURCHASE_CONFIRM);
        });
    }
}
