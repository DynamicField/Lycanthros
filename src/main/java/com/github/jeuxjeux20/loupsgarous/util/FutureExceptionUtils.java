package com.github.jeuxjeux20.loupsgarous.util;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;

public final class FutureExceptionUtils {
    private FutureExceptionUtils() {
    }

    public static boolean isCancellation(Throwable ex) {
        return ex instanceof CancellationException ||
               (ex instanceof CompletionException && ex.getCause() instanceof CancellationException);
    }

    public static CompletionException asCompletionException(Throwable ex) {
        return ex instanceof CompletionException ?
                (CompletionException) ex : new CompletionException(ex);
    }
}
