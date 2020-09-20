package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.google.common.reflect.TypeToken;

public class HandledExtensionPoint<T, H extends ExtensionPointHandler> extends ExtensionPoint<T> {
    private final Class<? extends H> handlerClass;

    public HandledExtensionPoint(String id, Class<T> valueType, Class<? extends H> handlerClass) {
        super(id, valueType);
        this.handlerClass = handlerClass;
    }

    public HandledExtensionPoint(String id, TypeToken<T> valueType, Class<? extends H> handlerClass) {
        super(id, valueType);
        this.handlerClass = handlerClass;
    }

    public Class<? extends H> getHandlerClass() {
        return handlerClass;
    }
}
