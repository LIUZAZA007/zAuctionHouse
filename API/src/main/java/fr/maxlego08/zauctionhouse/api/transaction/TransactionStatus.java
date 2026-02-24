package fr.maxlego08.zauctionhouse.api.transaction;

public enum TransactionStatus {

    PENDING("Pending"),
    RETRIEVED("Retrieved");

    private final String defaultDisplayName;

    TransactionStatus(String defaultDisplayName) {
        this.defaultDisplayName = defaultDisplayName;
    }

    public String getDefaultDisplayName() {
        return defaultDisplayName;
    }
}