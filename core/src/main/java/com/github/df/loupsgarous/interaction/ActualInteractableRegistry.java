package com.github.df.loupsgarous.interaction;

import com.github.df.loupsgarous.Registration;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import me.lucko.helper.terminable.Terminable;

public class ActualInteractableRegistry implements Terminable, InteractableRegistry {
    private final LGGameOrchestrator orchestrator;
    private final SetMultimap<String, Interactable> map =
            MultimapBuilder.hashKeys().hashSetValues().build();

    public ActualInteractableRegistry(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public ImmutableSet<Interactable> get(String key) {
        return ImmutableSet.copyOf(map.get(key));
    }

    @Override
    public SafeSingleBuilder<Interactable> single(String key) {
        return new SafeSingleBuilder<>(this, key, TypeToken.of(Interactable.class));
    }

    @Override
    public Registration register(String key, Interactable value) {
        boolean added = map.put(key, value);
        if (!added) {
            throw new UnsupportedOperationException("Cannot register a registered Interactable");
        }
        value.setKey(key);
        return new Registration() {
            @Override
            public void unregister() {
                ActualInteractableRegistry.this.unregister(value);
            }

            @Override
            public boolean isRegistered() {
                return map.containsEntry(value.getKey(), value);
            }
        };
    }

    @Override
    public void unregister(Interactable value) {
        map.get(value.getKey()).remove(value);
        value.setKey(null);
    }

    @Override
    public ImmutableSetMultimap<String, Interactable> getAll() {
        return ImmutableSetMultimap.copyOf(map);
    }

    @Override
    public void close() {
    }
}
