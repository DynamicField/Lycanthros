package com.github.jeuxjeux20.loupsgarous.config;

import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.ObjectMappingException;

public abstract class MappedBukkitConfigurationFile<T> extends BukkitConfigurationFile<T> {
    private T mappedObject;

    public MappedBukkitConfigurationFile(Plugin plugin) {
        super(plugin);
    }

    @Override
    public T get() {
        return mappedObject;
    }

    @Override
    protected void unsafeReload() throws Exception {
        try {
            super.unsafeReload();
            if (getRootNode().isEmpty()) {
                mappedObject = getDefaultValue();
            } else {
                mappedObject = getMapper().load(getRootNode());
            }
        } catch (Exception e) {
            mappedObject = getDefaultValue();
            throw e;
        }
    }

    @Override
    protected void unsafeSave() throws Exception {
        getMapper().save(mappedObject, getRootNode());
        super.unsafeSave();
    }

    protected abstract ObjectMapper<T> getMapper() throws ObjectMappingException;

    protected abstract T getDefaultValue();
}
