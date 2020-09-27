package com.github.jeuxjeux20.loupsgarous.extensibility;

public interface HasOrderingHint {
    Class<?> getOrderingInfoContainer();

    static Class<?> getContainerIn(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof HasOrderingHint) {
            return ((HasOrderingHint) object).getOrderingInfoContainer();
        } else {
            return object.getClass();
        }
    }
}
