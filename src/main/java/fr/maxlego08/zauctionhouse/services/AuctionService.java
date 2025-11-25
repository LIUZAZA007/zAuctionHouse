package fr.maxlego08.zauctionhouse.services;

import java.util.concurrent.CompletableFuture;

public abstract class AuctionService {

    /**
     * Return a failed CompletableFuture with the given exception.
     *
     * @param <T> the type of the future
     * @param ex  the exception to complete exceptionally
     * @return a failed CompletableFuture
     */
    protected <T> CompletableFuture<T> failedFuture(Throwable ex) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(ex);
        return future;
    }

}
