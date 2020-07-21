package com.github.jeuxjeux20.loupsgarous.game.cards.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGame;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CoupleCardRevealer implements CardRevealer {
    @Override
    public boolean willReveal(LGPlayer viewer, LGPlayer playerToReveal, LGGame game) {
        List<LGTeam> playerCouples = getCouples(playerToReveal);
        List<LGTeam> targetCouples = getCouples(viewer);

        return !Collections.disjoint(playerCouples, targetCouples);
    }

    private List<LGTeam> getCouples(LGPlayer player) {
        return player.getTeams().stream().filter(LGTeams::isCouple).collect(Collectors.toList());
    }
}
