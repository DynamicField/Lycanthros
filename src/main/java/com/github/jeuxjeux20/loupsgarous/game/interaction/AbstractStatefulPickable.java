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
import me.lucko.helper.event.MergedSubscription;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractStatefulPickable<T> extends AbstractPickable<T> implements StatefulPickable<T> {
    protected final LGGameOrchestrator orchestrator;

    private final Map<LGPlayer, T> picks = new HashMap<>();
    private final MergedSubscription<LGEvent> invalidateEventSubscription;

    public AbstractStatefulPickable(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;

        invalidateEventSubscription =
                Events.merge(LGEvent.class, ClassArrayUtils.toArray(getInvalidateEvents()))
                        .filter(orchestrator::isMyEvent)
                        .handler(e -> removeInvalidPicks());
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

        return safeRemovePick(picker, isInvalidate);
    }

    protected @Nullable T safeRemovePick(LGPlayer picker, boolean isInvalidate) {
        return picks.remove(picker);
    }

    public final boolean hasPick(LGPlayer picker) {
        throwIfClosed();

        return picks.containsKey(picker);
    }

    public final void removeInvalidPicks() {
        List<LGPlayer> invalidPicks = new ArrayList<>();

        picks.forEach((from, to) -> conditions().checkPick(from, to).ifError(e -> invalidPicks.add(from)));

        for (LGPlayer invalidPick : invalidPicks) {
            removePick(invalidPick, true);
        }
    }

    protected ImmutableList<Class<? extends LGEvent>> getInvalidateEvents() {
        return ImmutableList.of(LGKillEvent.class, LGPlayerQuitEvent.class);
    }

    @Override
    protected void closeResources() throws Exception {
        super.closeResources();
        invalidateEventSubscription.close();
    }
}
