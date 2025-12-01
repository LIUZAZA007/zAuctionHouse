package fr.maxlego08.zauctionhouse.api.configuration.records;

import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.utils.AuctionItemType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permissible;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public record PermissionConfiguration(EnumMap<AuctionItemType, Map<String, Integer>> permissions) {

    public static PermissionConfiguration of(AuctionPlugin plugin, FileConfiguration configuration) {
        EnumMap<AuctionItemType, Map<String, Integer>> permissions = new EnumMap<>(AuctionItemType.class);

        for (AuctionItemType auctionItemType : AuctionItemType.values()) {

            var elements = configuration.getMapList("permissions." + auctionItemType.name().toLowerCase());

            Map<String, Integer> localPermissions = new HashMap<>();
            for (Map<?, ?> element : elements) {
                TypedMapAccessor accessor = new TypedMapAccessor((Map<String, Object>) element);
                String permission = accessor.getString("permission");
                int limit = accessor.getInt("limit");
                localPermissions.put(permission, limit);
            }
            permissions.put(auctionItemType, localPermissions);
        }

        return new PermissionConfiguration(permissions);
    }

    public int getLimit(AuctionItemType auctionItemType, Permissible permissible) {
        return permissions.get(auctionItemType).entrySet().stream().filter(entry -> permissible.hasPermission(entry.getKey())).mapToInt(Map.Entry::getValue).max().orElse(0);
    }

}
