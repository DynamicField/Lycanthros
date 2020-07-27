package com.github.jeuxjeux20.loupsgarous.teams;

import com.github.jeuxjeux20.loupsgarous.teams.revealers.LGTeamRevealersModule;
import com.google.inject.AbstractModule;

public final class LGTeamsModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGTeamRevealersModule());
    }
}
