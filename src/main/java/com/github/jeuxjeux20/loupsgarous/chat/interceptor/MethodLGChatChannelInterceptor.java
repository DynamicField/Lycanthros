package com.github.jeuxjeux20.loupsgarous.chat.interceptor;

import com.github.jeuxjeux20.loupsgarous.chat.LGChatChannel;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Provider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * This method interceptor basically redirects method calls to the actual interceptor.
 */
final class MethodLGChatChannelInterceptor implements MethodInterceptor {
    private final Provider<? extends LGChatChannelInterceptor.Factory> interceptorFactoryProvider;
    private final Provider<Logger> loggerProvider;

    private boolean isInInterceptionContext;

    private final Cache<Method, Method> methodCache =
            CacheBuilder.newBuilder().concurrencyLevel(1).build();

    private final Cache<LGChatChannel, LGChatChannelInterceptor> interceptorCache =
            CacheBuilder.newBuilder().concurrencyLevel(1).build();

    MethodLGChatChannelInterceptor(Provider<? extends LGChatChannelInterceptor.Factory> interceptorFactoryProvider,
                                   Provider<Logger> loggerProvider) {
        this.interceptorFactoryProvider = interceptorFactoryProvider;
        this.loggerProvider = loggerProvider;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (isInInterceptionContext) {
            isInInterceptionContext = false;
            return invocation.proceed();
        }

        Object self = invocation.getThis();
        if (!(self instanceof LGChatChannel)) {
            Logger logger = loggerProvider.get();
            logger.warning("Tried to run a LGChatChannelInterceptor on class " + self.getClass() + " which is not " +
                           "a LGChatChannel! This is usually due to invalid matchers in bindChannelInterceptor.");

            return invocation.proceed();
        }

        LGChatChannel channel = (LGChatChannel) self;
        LGChatChannelInterceptor interceptor = interceptorCache.get(channel, () -> createInterceptor(channel));

        Method method = methodCache.get(invocation.getMethod(), () -> getMethodToInvoke(invocation, interceptor));

        if (method == invocation.getMethod()) {
            return invocation.proceed();
        } else {
            isInInterceptionContext = true;
            return method.invoke(interceptor, invocation.getArguments());
        }
    }

    private boolean isIgnoredMethod(Method interceptorMethod) {
        return interceptorMethod.isDefault() || interceptorMethod.isAnnotationPresent(Redirection.class);
    }

    private Method getMethodToInvoke(MethodInvocation invocation, LGChatChannelInterceptor interceptor) {
        Method invocationMethod = invocation.getMethod();
        Method interceptorMethod;

        try {
            interceptorMethod = interceptor.getClass().getMethod(invocationMethod.getName(),
                    invocationMethod.getParameterTypes());

            if (isIgnoredMethod(interceptorMethod)) {
                interceptorMethod = invocationMethod;
            }
        } catch (NoSuchMethodException e) {
            interceptorMethod = invocationMethod;
        }

        return interceptorMethod;
    }

    private LGChatChannelInterceptor createInterceptor(LGChatChannel channel) {
        return interceptorFactoryProvider.get().create(channel);
    }
}
