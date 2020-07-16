package com.github.jeuxjeux20.loupsgarous.game.descriptor;

import me.lucko.helper.metadata.MetadataMap;

public abstract class Descriptor<T> {
    private final Class<? extends T> describedClass;

    private final MetadataMap additionalProperties = MetadataMap.create();

    public Descriptor(Class<? extends T> describedClass) {
        this.describedClass = describedClass;
    }

    /**
     * Gets the class that this descriptor describes.
     *
     * @return the class that this descriptor describes
     */
    public Class<? extends T> getDescribedClass() {
        return describedClass;
    }

    public MetadataMap getAdditionalProperties() {
        return additionalProperties;
    }
}
