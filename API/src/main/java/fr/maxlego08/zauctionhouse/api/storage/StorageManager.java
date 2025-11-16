package fr.maxlego08.zauctionhouse.api.storage;

import org.bukkit.entity.Player;

public interface StorageManager {

    boolean onEnable();

    void onDisable();

    void upsertPlayer(Player player);

}
