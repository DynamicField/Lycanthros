package com.github.jeuxjeux20.loupsgarous.game.chat.interceptor;

import com.github.jeuxjeux20.loupsgarous.LGMatchers;
import com.github.jeuxjeux20.loupsgarous.Plugin;
import com.google.inject.*;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matcher;

import java.lang.annotation.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public abstract class ChatChannelInterceptorsModule extends AbstractModule {
    protected void bindChannelInterceptor(Matcher<? super Class<?>> channelMatcher,
                                          Class<? extends LGChatChannelInterceptor> interceptorClass) {
        bindChannelInterceptor(channelMatcher, TypeLiteral.get(interceptorClass));
    }

    protected void bindChannelInterceptor(Matcher<? super Class<?>> channelMatcher,
                                          TypeLiteral<? extends LGChatChannelInterceptor> interceptorType) {
        Key<LGChatChannelInterceptor.Factory> factoryKey = createFactoryBinding(interceptorType);

        MethodLGChatChannelInterceptor interceptor = createInterceptor(factoryKey);

        bindInterceptor(channelMatcher, LGMatchers.decorating(), interceptor);
    }

    private MethodLGChatChannelInterceptor createInterceptor(Key<LGChatChannelInterceptor.Factory> factoryKey) {
        Provider<LGChatChannelInterceptor.Factory> factoryProvider = getProvider(factoryKey);
        Provider<Logger> loggerProvider = getProvider(Key.get(Logger.class, Plugin.class));

        return new MethodLGChatChannelInterceptor(factoryProvider, loggerProvider);
    }

    private Key<LGChatChannelInterceptor.Factory> createFactoryBinding(
            TypeLiteral<? extends LGChatChannelInterceptor> interceptorType) {
        InterceptorFactory annotation = new InterceptorFactory.Impl();
        Key<LGChatChannelInterceptor.Factory> factoryKey = Key.get(LGChatChannelInterceptor.Factory.class, annotation);

        install(new FactoryModuleBuilder()
                .implement(LGChatChannelInterceptor.class, interceptorType)
                .build(factoryKey));

        return factoryKey;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
    @BindingAnnotation
    private @interface InterceptorFactory {
        int id();

        @SuppressWarnings("ClassExplicitlyAnnotation")
        class Impl implements InterceptorFactory {
            private static final AtomicInteger nextId = new AtomicInteger(1);

            private final int id;

            public Impl() {
                this.id = nextId.incrementAndGet();
            }

            @Override
            public int id() {
                return id;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return InterceptorFactory.class;
            }

            @Override
            public String toString() {
                return "@" +
                       InterceptorFactory.class.getName() +
                       "(id=" +
                       id +
                       ")";
            }

            @Override
            public boolean equals(Object o) {
                return o instanceof InterceptorFactory &&
                       ((InterceptorFactory) o).id() == id();
            }

            @Override
            public int hashCode() {
                return (127 * "id".hashCode()) ^ id;
            }
        }
    }
}
