package fr.maxlego08.zauctionhouse;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.storage.StorageManager;
import fr.maxlego08.zauctionhouse.listeners.PlayerListener;
import fr.maxlego08.zauctionhouse.storage.ZStorageManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ZAuctionPlugin extends JavaPlugin implements AuctionPlugin {

    private final StorageManager storageManager = new ZStorageManager(this);
    private boolean isEnabled = false;
    private PlatformScheduler platformScheduler;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        FoliaLib foliaLib = new FoliaLib(this);
        this.platformScheduler = foliaLib.getScheduler();

        if (!this.storageManager.onEnable()) return;

        this.addListener(new PlayerListener(this));

        isEnabled = true;
        this.getLogger().info("zAuctionHouse has just been loaded successfully!");
    }

    @Override
    public void onDisable() {

        if (!this.isEnabled) return;

        this.storageManager.onDisable();
    }

    @Override
    public PlatformScheduler getScheduler() {
        return this.platformScheduler;
    }

    @Override
    public StorageManager getStorageManager() {
        return this.storageManager;
    }

    private void addListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }
}