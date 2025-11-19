package fr.maxlego08.zauctionhouse.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AuctionEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    public AuctionEvent() {
        super();
    }

    public AuctionEvent(boolean isAsync) {
        super(isAsync);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}
