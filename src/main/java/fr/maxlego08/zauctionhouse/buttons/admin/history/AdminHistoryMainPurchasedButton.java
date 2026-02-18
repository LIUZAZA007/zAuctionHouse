package fr.maxlego08.zauctionhouse.buttons.admin.history;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdminHistoryMainPurchasedButton extends TargetHelper {

    public AdminHistoryMainPurchasedButton(Plugin plugin) {
        super((AuctionPlugin) plugin);
    }

    @Override
    public @Nullable ItemStack getCustomItemStack(@NotNull Player player, boolean useCache, @NotNull Placeholders placeholders) {
        getTargetName(player).ifPresent(target -> placeholders.register("target", target));
        return super.getCustomItemStack(player, useCache, placeholders);
    }

}
