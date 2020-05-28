package com.github.jeuxjeux20.loupsgarous.game.scoreboard;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class ScoreboardComponentsModule extends AbstractModule {
    private @Nullable Multibinder<ScoreboardComponent> scoreboardComponentBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureScoreboardComponents();
    }

    protected void configureBindings() {
    }

    protected void configureScoreboardComponents() {
    }

    private void actualConfigureScoreboardComponents() {
        scoreboardComponentBinder = Multibinder.newSetBinder(binder(), ScoreboardComponent.class);

        configureScoreboardComponents();
    }

    protected final void addScoreboardComponent(Class<? extends ScoreboardComponent> scoreboardComponent) {
        addScoreboardComponent(TypeLiteral.get(scoreboardComponent));
    }

    protected final void addScoreboardComponent(TypeLiteral<? extends ScoreboardComponent> scoreboardComponent) {
        Preconditions.checkState(scoreboardComponentBinder != null,
                "addScoreboardComponent can only be used inside configureScoreboardComponents()");

        scoreboardComponentBinder.addBinding().to(scoreboardComponent);
    }
}
