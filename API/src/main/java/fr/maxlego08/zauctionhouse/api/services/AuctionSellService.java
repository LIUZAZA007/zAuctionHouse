package fr.maxlego08.zauctionhouse.api.services;

import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public interface AuctionSellService {

    void sellAuctionItem(Player player, BigDecimal price, int amount, long expiredAt, ItemStack itemStack, AuctionEconomy auctionEconomy);
}
