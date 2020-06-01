package com.github.jeuxjeux20.loupsgarous.game.cards.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CoupleCardRevealer implements CardRevealer {
    @Override
    public boolean willReveal(LGPlayer playerToReveal, LGPlayer target, LGGameOrchestrator orchestrator) {
        List<LGTeam> playerCouples = getCouples(playerToReveal);
        List<LGTeam> targetCouples = getCouples(target);

        return !Collections.disjoint(playerCouples, targetCouples);
    }

    private List<LGTeam> getCouples(LGPlayer player) {
        return player.getCard().getTeams().stream().filter(LGTeams::isCouple).collect(Collectors.toList());
    }
}
