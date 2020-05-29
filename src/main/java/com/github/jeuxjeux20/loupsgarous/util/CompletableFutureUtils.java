package com.github.jeuxjeux20.loupsgarous.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class CompletableFutureUtils {
    private CompletableFutureUtils() {
    }
    
    public static <T> CompletableFuture<T> returnOriginal(CompletableFuture<T> future,
                                                          Consumer<? super CompletableFuture<T>> additionalOperations) {
        additionalOperations.accept(future);

        return future;
    }
}
