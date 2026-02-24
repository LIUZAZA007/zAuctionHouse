package fr.maxlego08.zauctionhouse.api.log;

public enum LogType {

    SALE("Sale"),
    PURCHASE("Purchase"),
    REMOVE_LISTED("Remove Listed"),
    REMOVE_OWNED("Remove Owned"),
    REMOVE_EXPIRED("Remove Expired"),
    REMOVE_PURCHASED("Remove Purchased");

    private final String defaultDisplayName;

    LogType(String defaultDisplayName) {
        this.defaultDisplayName = defaultDisplayName;
    }

    public String getDefaultDisplayName() {
        return defaultDisplayName;
    }
}
