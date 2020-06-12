package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.github.jeuxjeux20.loupsgarous.util.ClassArrayUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import me.lucko.helper.Events;
import me.lucko.helper.event.MergedSubscription;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

public interface StatefulPickableProvider extends PickableProvider {
    PickState getCurrentState();

    @Override
    default Pickable providePickable() {
        return getCurrentState();
    }

    class PickState implements Pickable, Terminable {
        protected final Map<LGPlayer, LGPlayer> picks = new Hashtable<>();

        private final LGGameOrchestrator orchestrator;
        private final StatefulPickableProvider me;

        private final MergedSubscription<LGEvent> invalidateEventSubscription;

        public PickState(LGGameOrchestrator orchestrator, StatefulPickableProvider me) {
            this.orchestrator = orchestrator;
            this.me = me;

            invalidateEventSubscription =
                    Events.merge(LGEvent.class, ClassArrayUtils.toArray(getInvalidateEvents()))
                            .filter(orchestrator::isMyEvent)
                            .handler(e -> removeInvalidPicks());
        }

        public final ImmutableMap<LGPlayer, LGPlayer> getPicks() {
            return ImmutableMap.copyOf(picks);
        }

        public Check canPickTarget(LGPlayer target) {
            if (target.isDead() && !canTargetBeDead()) return Check.error(getTargetDeadError(target));
            return Check.success();
        }

        public Check canPlayerPick(@NotNull LGPlayer player) {
            if (player.isDead()) return Check.error(getPickerDeadError());
            return Check.success();
        }

        protected boolean canTargetBeDead() {
            return false;
        }

        protected @NotNull String getTargetDeadError(@NotNull LGPlayer target) {
            return error("Impossible de choisir ") + player(target.getName()) + error(" car il est mort !");
        }

        protected @NotNull String getPickerDeadError() {
            return "Impossible d'agir, car vous êtes mort !";
        }

        public synchronized final void togglePick(@NotNull LGPlayer from, @NotNull LGPlayer to) {
            if (picks.get(from) == to) {
                removePick(from);
            } else {
                pick(from, to);
            }
        }

        public synchronized final void pick(@NotNull LGPlayer from, @NotNull LGPlayer to) {
            canPick(from, to).ifError(error -> {
                throw new IllegalArgumentException("Cannot pick player " + to.getName() + ", :" + error);
            });
            picks.put(from, to);
            orchestrator.callEvent(new LGPickEvent(orchestrator, me, from, to));
        }

        public synchronized final void removePick(@NotNull LGPlayer from) {
            LGPlayer removed = picks.remove(from);
            if (removed != null) {
                orchestrator.callEvent(new LGPickRemovedEvent(orchestrator, me, from, removed));
            }
        }

        public synchronized final boolean hasPick(@NotNull LGPlayer from) {
            return picks.containsKey(from);
        }

        public synchronized final void removeInvalidPicks() {
            List<LGPlayer> invalidPicks = new ArrayList<>();

            picks.forEach((from, to) -> canPick(from, to).ifError(e -> invalidPicks.add(from)));

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
}
