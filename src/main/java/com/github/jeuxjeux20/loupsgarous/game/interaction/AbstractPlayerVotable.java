package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

public abstract class AbstractPlayerVotable extends AbstractVotable<LGPlayer> {
    public AbstractPlayerVotable(LGGameOrchestrator orchestrator, InteractableKey<Votable<LGPlayer>> key) {
        super(orchestrator, key);
    }

    @Override
    public PickConditions<LGPlayer> conditions() {
        return FunctionalPickConditions.<LGPlayer>builder()
                .ensurePicker(LGPlayer::isAlive, this::getPickerDeadError)
                .ensureTarget(LGPlayer::isAlive, this::getTargetDeadError)
                .build();
    }

    protected String getTargetDeadError(LGPlayer target) {
        return error("Impossible de voter pour ") + player(target.getName()) + error(" car il est mort !");
    }

    protected String getPickerDeadError(LGPlayer picker) {
        return "Impossible de voter, car vous êtes mort !";
    }
}
