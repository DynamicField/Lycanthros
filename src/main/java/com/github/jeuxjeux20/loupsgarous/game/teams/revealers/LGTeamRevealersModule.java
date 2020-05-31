package com.github.jeuxjeux20.loupsgarous.game.teams.revealers;

public final class LGTeamRevealersModule extends TeamRevealersModule {
    @Override
    protected void configureTeamRevealers() {
        addTeamRevealer(LoupsGarousTeamRevealer.class);
    }
}
