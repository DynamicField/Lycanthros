package com.github.jeuxjeux20.loupsgarous.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents a simple object wrapper for an operation that can fail, with an error message.
 * <p>
 * This class is particularly useful when errors are expected, and removes the overhead of throwing exceptions.
 *
 * @param <T> the type of the result
 * @see Check
 */
public final class SafeResult<T> {
    public final @Nullable T value;
    public final boolean success;
    public final @Nullable String errorMessage;

    private SafeResult(@Nullable T value, boolean success, @Nullable String errorMessage) {
        if (success && value == null) {
            throw new IllegalArgumentException("The value must not be null when the operation is successful.");
        }
        if (errorMessage == null && success) errorMessage = "Error";

        this.value = value;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static <T> SafeResult<T> success(T value) {
        return new SafeResult<>(value, true, null);
    }

    public static <T> SafeResult<T> error(@Nullable String message) {
        return new SafeResult<>(null, false, message);
    }

    public @NotNull T getValue() {
        Preconditions.checkState(success, "The operation is not successful.");
        Preconditions.checkState(value != null, "The value must not be null.");
        return value;
    }

    public boolean isSuccess() {
        return success;
    }

    public @NotNull String getErrorMessage() {
        Preconditions.checkState(!success, "The operation must not be successful.");
        Preconditions.checkState(errorMessage != null, "The message must not be null.");
        return errorMessage;
    }

    public void ifSuccess(Consumer<T> consumer) {
        if (success) consumer.accept(value);
    }

    public void ifSuccessOrElse(Consumer<T> successConsumer, Consumer<String> errorConsumer) {
        if (success) successConsumer.accept(value);
        else errorConsumer.accept(errorMessage);
    }

    public void ifError(Consumer<String> consumer) {
        if (success) consumer.accept(errorMessage);
    }

    public Optional<T> asOptional() {
        return Optional.ofNullable(value);
    }
}
