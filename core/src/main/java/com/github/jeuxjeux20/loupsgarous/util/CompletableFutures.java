package com.github.jeuxjeux20.loupsgarous.util;

import java.util.concurrent.CompletableFuture;

public final class CompletableFutures {
    private CompletableFutures() {}

    public static <T> CompletableFuture<T> cancelledFuture() {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.cancel(true);
        return future;
    }
}
