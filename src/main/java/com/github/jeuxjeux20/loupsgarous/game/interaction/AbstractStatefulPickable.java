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
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractStatefulPickable<T> implements StatefulPickable<T>, Terminable {
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
    public void pick(@NotNull LGPlayer picker, @NotNull T target) {
        conditions().throwIfInvalid(picker, target);
        picks.put(picker, target);
    }

    public @Nullable T removePick(@NotNull LGPlayer from) {
        return picks.remove(from);
    }

    public synchronized final boolean hasPick(@NotNull LGPlayer from) {
        return picks.containsKey(from);
    }

    public synchronized final void removeInvalidPicks() {
        List<LGPlayer> invalidPicks = new ArrayList<>();

        picks.forEach((from, to) -> conditions().checkPick(from, to).ifError(e -> invalidPicks.add(from)));

        for (LGPlayer invalidPick : invalidPicks) {
            removePick(invalidPick);
        }
    }

    protected ImmutableList<Class<? extends LGEvent>> getInvalidateEvents() {
        return ImmutableList.of(LGKillEvent.class, LGPlayerQuitEvent.class);
    }

    @Override
    public void close() {
        invalidateEventSubscription.close();
    }
}
