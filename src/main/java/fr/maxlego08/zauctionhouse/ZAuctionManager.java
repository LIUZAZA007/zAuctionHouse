package fr.maxlego08.zauctionhouse;

import fr.maxlego08.zauctionhouse.api.AuctionManager;
import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.inventories.Inventories;
import fr.maxlego08.zauctionhouse.utils.ZUtils;
import org.bukkit.entity.Player;

public class ZAuctionManager extends ZUtils implements AuctionManager {

    private final AuctionPlugin plugin;

    public ZAuctionManager(AuctionPlugin plugin) {
        this.plugin = plugin;
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
}
