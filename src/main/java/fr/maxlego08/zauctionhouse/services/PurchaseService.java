package fr.maxlego08.zauctionhouse.services;

import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.services.AuctionPurchaseService;

public class PurchaseService implements AuctionPurchaseService {

    private final AuctionPlugin plugin;

    public PurchaseService(AuctionPlugin plugin) {
        this.plugin = plugin;
    }
}
