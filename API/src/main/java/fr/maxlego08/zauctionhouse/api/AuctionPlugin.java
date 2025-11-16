package fr.maxlego08.zauctionhouse.api;

import com.tcoded.folialib.impl.PlatformScheduler;
import fr.maxlego08.zauctionhouse.api.storage.StorageManager;
import org.bukkit.plugin.Plugin;

public interface AuctionPlugin extends Plugin {

    PlatformScheduler getScheduler();

    StorageManager getStorageManager();

}
