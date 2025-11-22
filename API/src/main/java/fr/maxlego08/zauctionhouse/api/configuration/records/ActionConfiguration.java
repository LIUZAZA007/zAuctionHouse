package fr.maxlego08.zauctionhouse.api.configuration.records;

import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public record ActionConfiguration(
        boolean giveItemAfterPurchase,
         boolean giveItemAfterRemoveRemoveFromListing,
         boolean openInventoryAfterRemoveFromListing
) {

    public static ActionConfiguration of(AuctionPlugin plugin, FileConfiguration configuration){
        return new ActionConfiguration(
                configuration.getBoolean("action.purchase.give-item"),
                configuration.getBoolean("action.remove-from-listing.give-item"),
                configuration.getBoolean("action.remove-from-listing.open-inventory")
        );
    }

}
