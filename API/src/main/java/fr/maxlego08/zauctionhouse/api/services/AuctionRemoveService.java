package fr.maxlego08.zauctionhouse.api.services;

import fr.maxlego08.zauctionhouse.api.items.Item;
import org.bukkit.entity.Player;

public interface AuctionRemoveService {

    void removeItemFromListing(Player player, Item item);

}
