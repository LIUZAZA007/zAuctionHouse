package fr.maxlego08.zauctionhouse.command.commands;

import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.messages.Message;
import fr.maxlego08.zauctionhouse.api.utils.Permission;
import fr.maxlego08.zauctionhouse.command.VCommand;
import fr.maxlego08.zauctionhouse.utils.commands.CommandType;

public class CommandAuctionPage extends VCommand {

    public CommandAuctionPage(AuctionPlugin plugin) {
        super(plugin);

        this.setPermission(Permission.ZAUCTIONHOUSE_USE);
        this.setDescription(Message.COMMAND_DESCRIPTION_AUCTION_PAGE);
        this.addSubCommand("page", "p");
        this.addRequireArg("page");
        this.setConsoleCanUse(false);
    }

    @Override
    protected CommandType perform(AuctionPlugin plugin) {
        int page = argAsInteger(0, 1);
        if (page < 1) page = 1;

        this.auctionManager.openMainAuction(this.player, page);
        return CommandType.SUCCESS;
    }
}
