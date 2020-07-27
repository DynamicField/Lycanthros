package com.github.jeuxjeux20.loupsgarous.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.PickConditions;

public final class CriticalPickableConditions {
    private CriticalPickableConditions() {}

    public static PickConditions<LGPlayer> player(LGGameOrchestrator orchestrator) {
        return FunctionalPickConditions.<LGPlayer>builder()
                .ensureTarget(PickableConditions.checkPlayerGamePresence(orchestrator))
                .build();
    }

    public static PickConditions<Couple> couple(LGGameOrchestrator orchestrator) {
        PickConditions<LGPlayer> criticalPlayerCondition = player(orchestrator);

        return FunctionalPickConditions.<Couple>builder()
                .use(criticalPlayerCondition.map(Couple::getPartner1))
                .use(criticalPlayerCondition.map(Couple::getPartner2))
                .build();
    }
}
