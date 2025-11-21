package fr.maxlego08.zauctionhouse.api.configuration.records;

import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public record ExpirationConfiguration(long defaultExpiration, boolean enablePermission, Map<String, Long> expirations) {

    public static ExpirationConfiguration of(AuctionPlugin plugin, FileConfiguration configuration, String path) {
        long defaultExpiration = configuration.getLong(path + "default-expiration");
        boolean enablePermission = configuration.getBoolean(path + "permission.enable");
        Map<String, Long> expirations = new HashMap<>();
        for (Map<?, ?> map : configuration.getMapList(path + "permission.permissions")) {
            TypedMapAccessor accessor = new TypedMapAccessor((Map<String, Object>) map);
            var permission = accessor.getString("permission");
            var expiration = accessor.getLong("expiration");
            if (permission == null) {
                plugin.getLogger().info("The permission is null for '" + path + "' !, you need to fix that ");
                continue;
            }

            expirations.put(permission, expiration);
        }

        return new ExpirationConfiguration(defaultExpiration, enablePermission, expirations);
    }

    public long getExpiration(Player player) {
        long expiration = this.defaultExpiration;
        if (this.enablePermission) {
            for (Map.Entry<String, Long> entry : this.expirations.entrySet()) {
                if (player.hasPermission(entry.getKey())) {
                    expiration = Math.max(expiration, entry.getValue());
                    break;
                }
            }
        }
        return expiration;
    }
}
