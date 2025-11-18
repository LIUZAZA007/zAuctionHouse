package fr.maxlego08.zauctionhouse.api;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.zauctionhouse.api.inventories.Inventories;
import org.bukkit.entity.Player;

import java.io.File;

public interface InventoriesLoader {

    void loadInventories();

    void loadInventory(File file);

    void loadPatterns();

    void loadPattern(File file);

    void loadButtons();

    void load();

    void reload();

    InventoryManager getInventoryManager();

    ButtonManager getButtonManager();

    void openInventory(Player player, Inventories inventories);

    void openInventory(Player player, Inventories inventories, int page);
}
