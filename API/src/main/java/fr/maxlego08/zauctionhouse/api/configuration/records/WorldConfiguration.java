package fr.maxlego08.zauctionhouse.api.configuration.records;

import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.utils.AuctionItemType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.EnumMap;
import java.util.List;

public record WorldConfiguration(EnumMap<AuctionItemType, List<String>> worlds) {

    public static WorldConfiguration of(AuctionPlugin plugin, FileConfiguration configuration) {
        EnumMap<AuctionItemType, List<String>> worlds = new EnumMap<>(AuctionItemType.class);

        for (AuctionItemType auctionItemType : AuctionItemType.values()) {
            var localWorlds = configuration.getStringList("banned-worlds." + auctionItemType.name().toLowerCase());
            worlds.put(auctionItemType, localWorlds);
        }

        return new WorldConfiguration(worlds);
    }

    public boolean isWorldBanned(AuctionItemType auctionItemType, String worldName) {
        return this.worlds.get(auctionItemType).contains(worldName);
    }

}
