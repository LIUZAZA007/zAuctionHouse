package fr.maxlego08.zauctionhouse.inventory;

import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public class SellInventoryHolder implements InventoryHolder {

    public static final int CONFIRM_SLOT = 48;
    public static final int CANCEL_SLOT = 50;

    private final UUID playerUniqueId;
    private final BigDecimal price;
    private final long expiredAt;
    private final AuctionEconomy auctionEconomy;
    private final Set<Integer> lockedSlots;
    private final int confirmSlot;
    private final int cancelSlot;
    private Inventory inventory;
    private boolean completed;

    public SellInventoryHolder(UUID playerUniqueId, BigDecimal price, long expiredAt, AuctionEconomy auctionEconomy, Set<Integer> lockedSlots, int confirmSlot, int cancelSlot) {
        this.playerUniqueId = playerUniqueId;
        this.price = price;
        this.expiredAt = expiredAt;
        this.auctionEconomy = auctionEconomy;
        this.lockedSlots = lockedSlots;
        this.confirmSlot = confirmSlot;
        this.cancelSlot = cancelSlot;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long getExpiredAt() {
        return expiredAt;
    }

    public AuctionEconomy getAuctionEconomy() {
        return auctionEconomy;
    }

    public Set<Integer> getLockedSlots() {
        return lockedSlots;
    }

    public boolean isLockedSlot(int slot) {
        return lockedSlots.contains(slot);
    }

    public int getConfirmSlot() {
        return confirmSlot;
    }

    public int getCancelSlot() {
        return cancelSlot;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
