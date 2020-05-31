package com.github.jeuxjeux20.loupsgarous.util;

import com.github.jeuxjeux20.loupsgarous.LGChatStuff;
import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.*;

/**
 * Checks are simple objects that represent if an operation can be completed or not, and provides
 * a user-friendly error message when it failed.
 * <p>
 * Most of the time, these are used as a replacement for {@code boolean} in methods such as {@code canDoX()}.
 */
public final class Check {
    private final boolean success;
    private final String errorMessage;

    private Check(boolean success, @Nullable String errorMessage) {
        if (success && errorMessage == null) errorMessage = "Error";

        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static Check success() {
        return new Check(true, null);
    }

    public static Check error(@Nullable String message) {
        return new Check(false, message);
    }

    public static Check ensure(boolean success, String errorMessage) {
        if (success) return Check.success();
        return Check.error(errorMessage);
    }

    public static <T> Predicate<? super T> predicate(Function<? super T, ? extends Check> checkFunction) {
        return x -> checkFunction.apply(x).isSuccess();
    }

    public boolean isSuccess() {
        return success;
    }

    public @NotNull String getErrorMessage() {
        Preconditions.checkState(!success, "The operation must not be successful.");
        return errorMessage;
    }

    public Optional<String> getErrorMessageOptional() {
        return Optional.ofNullable(errorMessage);
    }

    public void ifSuccess(Runnable runnable) {
        if (success) runnable.run();
    }

    public void ifSuccessOrElse(Runnable successRunnable, Consumer<String> errorConsumer) {
        if (success) successRunnable.run();
        else errorConsumer.accept(errorMessage);
    }

    public Check and(boolean success, String errorMessage) {
        return and(Check.ensure(success, errorMessage));
    }

    public Check and(BooleanSupplier successSupplier, String errorMessage) {
        return and(() -> Check.ensure(successSupplier.getAsBoolean(), errorMessage));
    }

    public Check and(Check other) {
        if (!this.success) return this;
        else return other;
    }

    public Check and(Supplier<Check> other) {
        if (!this.success) return this;
        else return other.get();
    }

    public boolean sendMessageOnError(Player player) {
        if (success) return false;
        player.sendMessage(LGChatStuff.error(errorMessage));
        return true;
    }

    public void ifError(Consumer<String> consumer) {
        if (!success) consumer.accept(errorMessage);
    }
}
