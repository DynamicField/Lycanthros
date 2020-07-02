package com.github.jeuxjeux20.loupsgarous.game.interaction.vote;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.CriticalPickableConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import java.util.List;
import java.util.Map;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

public abstract class AbstractPlayerVotable extends AbstractVotable<LGPlayer> {
    public AbstractPlayerVotable(LGGameOrchestrator orchestrator, PlayerVoteDependencies dependencies) {
        super(orchestrator, dependencies);
    }

    @Override
    public final PickConditions<LGPlayer> pickConditions() {
        return FunctionalPickConditions.<LGPlayer>builder()
                .use(defaultVoteConditions())
                .use(additionalVoteConditions())
                .build();
    }

    protected PickConditions<LGPlayer> defaultVoteConditions() {
        return FunctionalPickConditions.<LGPlayer>builder()
                .ensurePicker(LGPlayer::isAlive, this::getPickerDeadError)
                .ensureTarget(LGPlayer::isAlive, this::getTargetDeadError)
                .build();
    }

    protected abstract PickConditions<LGPlayer> additionalVoteConditions();

    @Override
    protected final PickConditions<LGPlayer> criticalConditions() {
        return CriticalPickableConditions.player(orchestrator);
    }

    protected String getTargetDeadError(LGPlayer target) {
        return error("Impossible de voter pour ") + player(target.getName()) + error(" car il est mort !");
    }

    protected String getPickerDeadError(LGPlayer picker) {
        return "Impossible de voter, car vous êtes mort !";
    }

    public static final class PlayerVoteDependencies extends AbstractVotable.Dependencies<LGPlayer> {
        @Inject
        PlayerVoteDependencies(Map<TypeLiteral<?>, List<VoteOutcomeModifier<?>>> voteOutcomeModifierMap,
                               TypeLiteral<LGPlayer> candidateType) {
            super(voteOutcomeModifierMap, candidateType);
        }
    }
}
