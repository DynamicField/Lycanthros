package com.github.jeuxjeux20.loupsgarous.game.teams.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.google.common.collect.ImmutableSet;

public interface TeamRevealer {
    ImmutableSet<LGTeam> getTeamsRevealed(LGPlayer viewer, LGPlayer playerToReveal, LGGameOrchestrator orchestrator);
}
