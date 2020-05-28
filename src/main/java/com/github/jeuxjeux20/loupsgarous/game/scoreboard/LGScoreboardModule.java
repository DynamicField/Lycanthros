package com.github.jeuxjeux20.loupsgarous.game.scoreboard;

public final class LGScoreboardModule extends ScoreboardComponentsModule {
    @Override
    protected void configureBindings() {
        bind(LGScoreboardManager.class).to(MinecraftLGScoreboardManager.class);
    }

    @Override
    protected void configureScoreboardComponents() {
        addScoreboardComponent(LobbyOwnerScoreboardComponent.class);
        addScoreboardComponent(PlayersAliveScoreboardComponent.class);
        addScoreboardComponent(CompositionScoreboardComponent.class);
        addScoreboardComponent(CurrentVotesScoreboardComponent.class);
    }
}
