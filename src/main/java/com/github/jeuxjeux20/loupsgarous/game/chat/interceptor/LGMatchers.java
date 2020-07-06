package com.github.jeuxjeux20.loupsgarous.game.chat.interceptor;

import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

public final class LGMatchers {
    private LGMatchers() {
    }

    public static Matcher<Class<?>> exactClass(Class<?> clazz) {
        return new ExactClassMatcher(clazz);
    }

    public static Matcher<Method> decorating() {
        return DECORATING;
    }

    private static final DecoratingMatcher DECORATING = new DecoratingMatcher();

    private static final class DecoratingMatcher extends AbstractMatcher<Method> {
        @Override
        public boolean matches(Method method) {
            return method.getDeclaringClass() != Object.class && Modifier.isPublic(method.getModifiers());
        }

        @Override
        public String toString() {
            return "decorating()";
        }
    }

    private static final class ExactClassMatcher extends AbstractMatcher<Class<?>> {
        private final Class<?> clazz;

        private ExactClassMatcher(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean matches(Class<?> aClass) {
            return aClass == clazz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExactClassMatcher that = (ExactClassMatcher) o;
            return Objects.equals(clazz, that.clazz);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz);
        }

        @Override
        public String toString() {
            return "exactClass(" + clazz.getSimpleName() + ".class)";
        }
    }
}