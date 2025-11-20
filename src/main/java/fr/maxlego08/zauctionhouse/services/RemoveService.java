package fr.maxlego08.zauctionhouse.services;

import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.services.AuctionRemoveService;
import fr.maxlego08.zauctionhouse.api.services.AuctionSellService;

public class RemoveService implements AuctionRemoveService {

    private final AuctionPlugin plugin;

    public RemoveService(AuctionPlugin plugin) {
        this.plugin = plugin;
    }
}
