package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.condition.PickConditions;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

public abstract class AbstractPlayerVotable extends AbstractVotable<LGPlayer> implements PlayerVotable {
    public AbstractPlayerVotable(LGGameOrchestrator orchestrator) {
        super(orchestrator);
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
