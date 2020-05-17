package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.events.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;
import java.util.Map;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

public interface StatefulPickableProvider extends PickableProvider {
    PickState getCurrentState();

    @Override
    default Pickable providePickable() {
        return getCurrentState();
    }

    class PickState implements Pickable {
        protected final Map<LGPlayer, LGPlayer> picks = new Hashtable<>();
        private final LGGameOrchestrator orchestrator;
        private final StatefulPickableProvider me;

        public PickState(LGGameOrchestrator orchestrator, StatefulPickableProvider me) {
            this.orchestrator = orchestrator;
            this.me = me;
        }

        public final ImmutableMap<LGPlayer, LGPlayer> getPicks() {
            return ImmutableMap.copyOf(picks);
        }

        public Check canPick(@NotNull LGPlayer from, @NotNull LGPlayer to) {
            return canPlayerPick(from).and(() -> {
                if (to.isDead() && !canTargetBeDead()) return Check.error(getTargetDeadError(to));
                return Check.success();
            });
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
    }
}
