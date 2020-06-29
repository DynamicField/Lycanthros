package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

public abstract class AbstractPlayerVotable<E extends Pickable<LGPlayer>> extends AbstractVotable<LGPlayer, E> {
    public AbstractPlayerVotable(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public final PickConditions<LGPlayer> conditions() {
        return FunctionalPickConditions.<LGPlayer>builder()
                .use(defaultConditions())
                .use(additionalConditions())
                .build();
    }

    protected PickConditions<LGPlayer> defaultConditions() {
        return FunctionalPickConditions.<LGPlayer>builder()
                .ensurePicker(LGPlayer::isAlive, this::getPickerDeadError)
                .ensureTarget(LGPlayer::isAlive, this::getTargetDeadError)
                .build();
    }

    protected PickConditions<LGPlayer> additionalConditions() {
        return PickConditions.empty();
    }

    protected String getTargetDeadError(LGPlayer target) {
        return error("Impossible de voter pour ") + player(target.getName()) + error(" car il est mort !");
    }

    protected String getPickerDeadError(LGPlayer picker) {
        return "Impossible de voter, car vous êtes mort !";
    }
}
