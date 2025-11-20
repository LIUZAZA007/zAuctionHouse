package fr.maxlego08.zauctionhouse.items;

import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import fr.maxlego08.zauctionhouse.api.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public abstract class ZItem implements Item {

    protected final AuctionPlugin plugin;
    protected final int id;
    protected final UUID sellerUniqueId;
    protected final String sellerName;
    protected final BigDecimal price;
    protected final AuctionEconomy auctionEconomy;
    protected final Date createdAt;
    protected final Date expiredAt;

    public ZItem(AuctionPlugin plugin, int id, UUID sellerUniqueId, String sellerName, BigDecimal price, AuctionEconomy auctionEconomy, Date createdAt, Date expiredAt) {
        this.plugin = plugin;
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
