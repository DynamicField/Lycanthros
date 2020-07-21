package com.github.jeuxjeux20.loupsgarous.game;

import com.google.common.base.Preconditions;
import com.google.inject.*;

import java.util.HashMap;
import java.util.Map;

public final class OrchestratorScope implements Scope {
    private static final Provider<Object> SEEDED_KEY_PROVIDER =
            () -> {
                throw new IllegalStateException("Impossible to retrieve the requested dependency while " +
                                                "not running in an OrchestratorScope.");
            };

    private static final Key<InternalLGGameOrchestrator> MUTABLE_ORCHESTRATOR_KEY =
            Key.get(InternalLGGameOrchestrator.class);

    private static final Key<LGGameOrchestrator> ORCHESTRATOR_KEY =
            Key.get(LGGameOrchestrator.class);

    private final ThreadLocal<Map<Key<?>, Object>> values = new ThreadLocal<>();

    /**
     * Returns a provider that always throws exception complaining that the object
     * in question must be seeded before it can be injected.
     *
     * @return typed provider
     */
    @SuppressWarnings("unchecked")
    public static <T> Provider<T> seededKeyProvider() {
        return (Provider<T>) SEEDED_KEY_PROVIDER;
    }

    public void enter(LGGameOrchestrator orchestrator) {
        Preconditions.checkState(values.get() == null, "A scoping block is already in progress");

        Map<Key<?>, Object> map = createInitialMap(orchestrator);
        values.set(map);
    }

    public void exit() {
        Preconditions.checkState(values.get() != null, "No scoping block in progress");

        values.remove();
    }

    public Block use(LGGameOrchestrator orchestrator) {
        Preconditions.checkState(values.get() == null, "A scoping block is already in progress");

        enter(orchestrator);
        return new Block(orchestrator);
    }

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return () -> {
            Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

            @SuppressWarnings("unchecked")
            T current = (T) scopedObjects.get(key);
            if (current == null && !scopedObjects.containsKey(key)) {
                current = unscoped.get();

                // don't remember proxies; these exist only to serve circular dependencies
                if (Scopes.isCircularProxy(current)) {
                    return current;
                }

                scopedObjects.put(key, current);
            }
            return current;
        };
    }

    @Override
    public String toString() {
        return "OrchestratorScope";
    }

    private Map<Key<?>, Object> createInitialMap(LGGameOrchestrator orchestrator) {
        HashMap<Key<?>, Object> map = new HashMap<>();
        map.put(ORCHESTRATOR_KEY, orchestrator);
        if (orchestrator instanceof InternalLGGameOrchestrator) {
            map.put(MUTABLE_ORCHESTRATOR_KEY, orchestrator);
        }
        return map;
    }

    private <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
        Map<Key<?>, Object> scopedObjects = values.get();
        if (scopedObjects == null) {
            throw new OutOfScopeException("Cannot access " + key
                                          + " outside of a scoping block");
        }
        return scopedObjects;
    }

    public class Block implements AutoCloseable {
        private final LGGameOrchestrator orchestrator;

        private Block(LGGameOrchestrator orchestrator) {
            this.orchestrator = orchestrator;
        }

        public LGGameOrchestrator getOrchestrator() {
            return orchestrator;
        }

        @Override
        public void close() {
            OrchestratorScope.this.exit();
        }
    }
}
