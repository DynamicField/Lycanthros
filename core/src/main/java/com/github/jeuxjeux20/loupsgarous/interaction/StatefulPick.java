package com.github.jeuxjeux20.loupsgarous.interaction;

import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.collect.ImmutableMap;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class StatefulPick<T> extends Pick<T> {
    private final Map<LGPlayer, T> picks = new HashMap<>();

    private Terminable invalidationSubscription = Terminable.EMPTY;

    public StatefulPick(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }


    public final ImmutableMap<LGPlayer, T> getPicks() {
        return ImmutableMap.copyOf(picks);
    }

    @Override
    protected void safePick(LGPlayer picker, T target) {
        picks.put(picker, target);
    }

    public final void removePick(LGPlayer picker) {
        removePick(picker, false);
    }

    protected final void removePick(LGPlayer picker, boolean isInvalidate) {
        Objects.requireNonNull(picker, "picker is null");

        safeRemovePick(picker, isInvalidate);
    }

    protected @Nullable T safeRemovePick(LGPlayer picker, boolean isInvalidate) {
        return picks.remove(picker);
    }

    public final boolean hasPick(LGPlayer picker) {
        return picks.containsKey(picker);
    }

    @Override
    protected void onRegister() {
        super.onRegister();
        registerInvalidationEvents();
    }

    @Override
    protected void onUnregister() {
        super.onUnregister();
        invalidationSubscription.closeAndReportException();
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

    private void registerInvalidationEvents() {
        invalidationSubscription =
                Events.merge(LGEvent.class, LGKillEvent.class, LGPlayerQuitEvent.class)
                        .filter(orchestrator::isMyEvent)
                        .handler(e -> removeInvalidPicks());
    }

    public void togglePick(LGPlayer picker, T target) {
        if (getPicks().get(picker) == target) {
            removePick(picker);
        } else {
            pick(picker, target);
        }
    }
}
