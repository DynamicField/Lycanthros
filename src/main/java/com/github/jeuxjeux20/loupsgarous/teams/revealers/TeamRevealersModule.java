package com.github.jeuxjeux20.loupsgarous.teams.revealers;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class TeamRevealersModule extends AbstractModule {
    private @Nullable Multibinder<TeamRevealer> teamRevealerBinder;

    @Override
    protected final void configure() {
        bind(TeamRevealer.class).to(TeamRevealerAggregator.class);

        configureBindings();
        actualConfigureTeamRevealers();
    }

    protected void configureBindings() {
    }

    protected void configureTeamRevealers() {
    }

    private void actualConfigureTeamRevealers() {
        teamRevealerBinder = Multibinder.newSetBinder(binder(), TeamRevealer.class);

        configureTeamRevealers();
    }

    protected final void addTeamRevealer(Class<? extends TeamRevealer> teamRevealer) {
        addTeamRevealer(TypeLiteral.get(teamRevealer));
    }

    protected final void addTeamRevealer(TypeLiteral<? extends TeamRevealer> teamRevealer) {
        Preconditions.checkState(teamRevealerBinder != null, "addTeamRevealer can only be used inside configureTeamRevealers()");

        teamRevealerBinder.addBinding().to(teamRevealer);
    }
}
