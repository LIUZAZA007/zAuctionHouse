package fr.maxlego08.zauctionhouse.items;

import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import fr.maxlego08.zauctionhouse.api.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public abstract class ZItem implements Item {

    private final int id;
    private final UUID sellerUniqueId;
    private final String sellerName;
    private final BigDecimal price;
    private final AuctionEconomy auctionEconomy;
    private final Date createdAt;
    private final Date expiredAt;

    public ZItem(int id, UUID sellerUniqueId, String sellerName, BigDecimal price, AuctionEconomy auctionEconomy, Date createdAt, Date expiredAt) {
        this.id = id;
        this.sellerUniqueId = sellerUniqueId;
        this.sellerName = sellerName;
        this.price = price;
        this.auctionEconomy = auctionEconomy;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public UUID getSellerUniqueId() {
        return sellerUniqueId;
    }

    @Override
    public String getSellerName() {
        return sellerName;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public AuctionEconomy getAuctionEconomy() {
        return auctionEconomy;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public Date getExpiredAt() {
        return expiredAt;
    }

    @Override
    public OfflinePlayer getSeller() {
        return Bukkit.getOfflinePlayer(this.sellerUniqueId);
    }
}
