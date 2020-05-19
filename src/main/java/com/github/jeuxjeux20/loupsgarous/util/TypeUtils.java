package com.github.jeuxjeux20.loupsgarous.util;

import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;

public final class TypeUtils {
    private TypeUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeLiteral<T> toLiteral(TypeToken<T> token) {
        return (TypeLiteral<T>) TypeLiteral.get(token.getType());
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeToken<T> toToken(TypeLiteral<T> token) {
        return (TypeToken<T>) TypeToken.of(token.getType());
    }
}
