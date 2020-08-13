package com.github.jeuxjeux20.loupsgarous.extensibility;

public interface ObjectFactory {
    <T> T create(Class<T> clazz);
}
