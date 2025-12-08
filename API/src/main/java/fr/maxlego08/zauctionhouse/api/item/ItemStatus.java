package fr.maxlego08.zauctionhouse.api.item;

public enum ItemStatus {

    AVAILABLE, // L'item est disponible à la vente ou pour être retiré

    IS_REMOVE_CONFIRM,
    IS_PURCHASE_CONFIRM,

    IS_BEING_REMOVED, // L'item est en train d'être retiré
    IS_BEING_PURCHASED, // L'item est en train d'être retiré

    REMOVED, // L'item a été retiré
    PURCHASED, // L'item a été acheté

    DELETED, // L'item a supprimé

    ;


}
