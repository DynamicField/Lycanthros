package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.relativesorting.OrderedElement;

public interface Sortable<T extends Sortable<T>> {
    OrderedElement<? extends T> getOrderedElement();
}
