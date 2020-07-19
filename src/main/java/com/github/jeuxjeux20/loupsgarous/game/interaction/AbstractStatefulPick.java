package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.util.ClassArrayUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import me.lucko.helper.Events;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractStatefulPick<T> extends AbstractPick<T> implements StatefulPick<T> {
    private final Map<LGPlayer, T> picks = new HashMap<>();

    public AbstractStatefulPick(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        registerInvalidationEvents(orchestrator);
    }

    public final ImmutableMap<LGPlayer, T> getPicks() {
        return ImmutableMap.copyOf(picks);
    }

    @Override
    protected void safePick(LGPlayer picker, T target) {
        picks.put(picker, target);
    }

    public final @Nullable T removePick(LGPlayer picker) {
        return removePick(picker, false);
    }

    protected final @Nullable T removePick(LGPlayer picker, boolean isInvalidate) {
        throwIfClosed();
        Objects.requireNonNull(picker, "picker is null");

        return safeRemovePick(picker, isInvalidate);
    }

    protected @Nullable T safeRemovePick(LGPlayer picker, boolean isInvalidate) {
        return picks.remove(picker);
    }

    public final boolean hasPick(LGPlayer picker) {
        return picks.containsKey(picker);
    }

    // Invalidation
    public final void removeInvalidPicks() {
        List<LGPlayer> invalidPicks = new ArrayList<>();

        picks.forEach((from, to) -> conditions().checkPick(from, to)
                .ifError(e -> invalidPicks.add(from)));

        for (LGPlayer invalidPick : invalidPicks) {
            removePick(invalidPick, true);
        }
    }

    private void registerInvalidationEvents(LGGameOrchestrator orchestrator) {
        Events.merge(LGEvent.class, ClassArrayUtils.toArray(getInvalidateEvents()))
                .filter(orchestrator::isMyEvent)
                .handler(e -> removeInvalidPicks())
                .bindWith(this);
    }

    protected ImmutableList<Class<? extends LGEvent>> getInvalidateEvents() {
        return ImmutableList.of(LGKillEvent.class, LGPlayerQuitEvent.class);
    }
}
