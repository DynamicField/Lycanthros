package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Objects;

public class ExtensionPoint<T> {
    private final String id;

    public ExtensionPoint(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public ImmutableList<T> getContents(LGGameOrchestrator orchestrator) {
        return orchestrator.getGameBox().contents(this);
    }

    public ImmutableSet<Extension<T>> getExtensions(LGGameOrchestrator orchestrator) {
        return orchestrator.getGameBox().extensions(this);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtensionPoint<?> that = (ExtensionPoint<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
