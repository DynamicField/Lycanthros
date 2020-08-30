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

    private static final Key<LGGameOrchestrator> ORCHESTRATOR_KEY =
            Key.get(LGGameOrchestrator.class);

    private final ThreadLocal<Map<Key<?>, Object>> values = new ThreadLocal<>();
    private final ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);

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
        if (depth.get() == 0) {
            Map<Key<?>, Object> map = createInitialMap(orchestrator);
            values.set(map);
        }
        else {
            if (values.get().get(ORCHESTRATOR_KEY) != orchestrator) {
                throw new IllegalArgumentException(
                        "The orchestrator isn't the same as the current scoping block.");
            }
        }
        depth.set(depth.get() + 1);
    }

    public void exit() {
        Preconditions.checkState(values.get() != null, "No scoping block in progress");

        if (depth.get() == 1) {
            values.remove();
        }
        depth.set(depth.get() - 1);
    }

    public Block use(LGGameOrchestrator orchestrator) {
        enter(orchestrator);
        return new Block();
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
        @Override
        public void close() {
            OrchestratorScope.this.exit();
        }
    }
}
