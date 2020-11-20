package com.github.jeuxjeux20.loupsgarous.interaction.vote;

import com.github.jeuxjeux20.loupsgarous.Check;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.CriticalPickableConditions;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.PickConditions;

import java.util.stream.Stream;

import static com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff.error;
import static com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff.player;

public abstract class PlayerVote extends Vote<LGPlayer> {
    public PlayerVote(LGGameOrchestrator orchestrator) {
        super(orchestrator, LGPlayer.class);
    }

    @Override
    public final PickConditions<LGPlayer> pickConditions() {
        return conditionsBuilder()
                .use(defaultVoteConditions())
                .use(additionalVoteConditions())
                .build();
    }

    protected PickConditions<LGPlayer> defaultVoteConditions() {
        return conditionsBuilder()
                .ensurePicker(LGPlayer::isAlive, this::getPickerDeadError)
                .ensureTarget(LGPlayer::isAlive, this::getTargetDeadError)
                .build();
    }

    protected abstract PickConditions<LGPlayer> additionalVoteConditions();

    @Override
    protected final PickConditions<LGPlayer> criticalConditions() {
        return CriticalPickableConditions.player(orchestrator);
    }

    public Stream<LGPlayer> getEligibleTargets() {
        return orchestrator.getPlayers().stream().filter(Check.predicate(conditions()::checkTarget));
    }

    protected String getTargetDeadError(LGPlayer target) {
        return error("Impossible de voter pour ") + player(target.getName()) + error(" car il est mort !");
    }

    protected String getPickerDeadError(LGPlayer picker) {
        return "Impossible de voter, car vous êtes mort !";
    }
}
