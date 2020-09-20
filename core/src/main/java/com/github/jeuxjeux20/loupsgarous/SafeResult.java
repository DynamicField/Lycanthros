package com.github.jeuxjeux20.loupsgarous;

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
    public final T value;

    private final Check check;

    private SafeResult(@Nullable T value, Check check) {
        if (check.isSuccess() && value == null) {
            throw new IllegalArgumentException("The value must not be null when the operation is successful.");
        }

        this.value = value;
        this.check = check;
    }

    public static <T> SafeResult<T> success(T value) {
        return new SafeResult<>(value, Check.success());
    }

    public static <T> SafeResult<T> error(@Nullable String message) {
        return new SafeResult<>(null, Check.error(message));
    }

    public @NotNull T getValue() {
        Preconditions.checkState(check.isSuccess(), "The operation is not successful.");
        return value;
    }

    public Optional<T> getValueOptional() {
        return Optional.ofNullable(value);
    }

    public boolean isError() {
        return !check.isSuccess();
    }

    public boolean isSuccess() {
        return check.isSuccess();
    }

    public @NotNull String getErrorMessage() {
        return check.getErrorMessage();
    }

    public Optional<String> getErrorMessageOptional() {
        return check.getErrorMessageOptional();
    }

    public void ifSuccess(Consumer<T> consumer) {
        if (check.isSuccess()) consumer.accept(value);
    }

    public void ifSuccessOrElse(Consumer<T> successConsumer, Consumer<String> errorConsumer) {
        if (check.isSuccess()) successConsumer.accept(value);
        else errorConsumer.accept(check.getErrorMessage());
    }

    public void ifError(Consumer<String> consumer) {
        if (!check.isSuccess()) consumer.accept(check.getErrorMessage());
    }
}
