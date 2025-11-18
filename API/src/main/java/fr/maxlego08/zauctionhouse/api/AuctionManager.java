package fr.maxlego08.zauctionhouse.api;

import org.bukkit.entity.Player;

public interface AuctionManager {

    void openMainAuction(Player player);

    void openMainAuction(Player player, int page);

}
