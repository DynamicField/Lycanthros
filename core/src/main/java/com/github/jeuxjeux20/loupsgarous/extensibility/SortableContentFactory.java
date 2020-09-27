package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

import java.lang.reflect.Constructor;

public interface SortableContentFactory<T> extends ContentFactory<T>, HasOrderingHint {
    @Override
    Class<?> getOrderingInfoContainer();

    static <T> SortableContentFactory<T> createFactory(Class<? extends T> clazz) {
        return new SortableContentFactory<T>() {
            private final Constructor<? extends T> constructor;

            {
                try {
                    this.constructor = clazz.getConstructor(LGGameOrchestrator.class);
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException(
                            "Class " + clazz + "does not have a public constructor " +
                            "taking a LGGameOrchestrator argument."
                    );
                }
            }

            @Override
            public T create(LGGameOrchestrator gameOrchestrator) {
                try {
                    return constructor.newInstance(gameOrchestrator);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate " + clazz + ".", e);
                }
            }

            @Override
            public Class<?> getOrderingInfoContainer() {
                return clazz;
            }

            @Override
            public String toString() {
                return clazz.getName();
            }
        };
    }
}
