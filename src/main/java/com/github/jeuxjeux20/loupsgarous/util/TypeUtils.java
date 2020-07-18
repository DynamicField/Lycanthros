package com.github.jeuxjeux20.loupsgarous.util;

import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class TypeUtils {
    private TypeUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeLiteral<T> toLiteralUnchecked(Type type) {
        return (TypeLiteral<T>) TypeLiteral.get(type);
    }

    public static <T> TypeLiteral<T> toLiteral(Class<T> type) {
        return TypeLiteral.get(type);
    }

    public static Type genericArgument(TypeLiteral<?> type, int index) {
        return genericArgument(type.getType(), index);
    }

    public static Type genericArgument(Type type, int index) {
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Type is not a parameterized type.");
        }
        return ((ParameterizedType) type).getActualTypeArguments()[index];
    }

    public static Type parameterized(Type type, Type... genericArguments) {
        Class<?> enclosingClass = null;
        if (type instanceof Class<?>) {
            enclosingClass = ((Class<?>) type).getEnclosingClass();
        }
        return Types.newParameterizedTypeWithOwner(enclosingClass, type, genericArguments);
    }
}
