package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.endings.CoupleWonEnding;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.teams.CoupleTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class CoupleWinCondition implements WinCondition {
    @Override
    public Optional<LGEnding> check(LGGameOrchestrator orchestrator) {
        List<LGPlayer> alivePlayers = orchestrator.getGame().getAlivePlayers().collect(Collectors.toList());
        CoupleTeam couple = null;

        // Note that this approach will not work if there are
        // two couples on two players, but in what kind of parallel universe
        // does someone have two couples with the same person anyway?
        for (LGPlayer player : alivePlayers) {
            List<LGTeam> playerCouples = player.getCard().getTeams().stream()
                    .filter(LGTeams::isCouple).collect(Collectors.toList());

            // There can only be ONE couple.
            if (playerCouples.size() != 1) {
                // If there isn't only one couple, then this isn't a win.
                return Optional.empty();
            } else {
                CoupleTeam currentCouple = (CoupleTeam) playerCouples.get(0);
                if (couple != null && !couple.equals(currentCouple)) {
                    // Not the same couple (somehow there are multiple ones).
                    return Optional.empty();
                } else {
                    couple = currentCouple;
                }
            }
        }

        // No couple at all? Probably because everyone's dead...
        if (couple == null) {
            return Optional.empty();
        }

        // Every player had exactly one couple, and it was the same for everyone.
        // Congrats!
        return Optional.of(new CoupleWonEnding(couple));
    }
}
