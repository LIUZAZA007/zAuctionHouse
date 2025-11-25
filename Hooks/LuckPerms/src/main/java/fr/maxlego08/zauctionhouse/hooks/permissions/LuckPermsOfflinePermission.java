package fr.maxlego08.zauctionhouse.hooks.permissions;

import fr.maxlego08.zauctionhouse.api.hooks.permission.OfflinePermission;
import fr.maxlego08.zauctionhouse.api.hooks.permission.OfflinePermissionResult;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Set;

public class LuckPermsOfflinePermission implements OfflinePermission {

    private final LuckPerms luckPerms;

    public LuckPermsOfflinePermission() {
        this.luckPerms = LuckPermsProvider.get();
    }

    @Override
    public List<OfflinePermissionResult> hasPermissions(OfflinePlayer offlinePlayer, Set<String> permissions) {
        User user = this.luckPerms.getUserManager().loadUser(offlinePlayer.getUniqueId()).join();
        if (user == null) {
            return permissions.stream().map(permission -> new OfflinePermissionResult(permission, false)).toList();
        }

        var permissionData = user.getCachedData().getPermissionData(this.luckPerms.getContextManager().getStaticQueryOptions());

        return permissions.stream().map(permission -> new OfflinePermissionResult(permission, permissionData.checkPermission(permission).asBoolean())).toList();
    }
}
