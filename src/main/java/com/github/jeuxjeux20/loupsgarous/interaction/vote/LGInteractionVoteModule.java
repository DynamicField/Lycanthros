package com.github.jeuxjeux20.loupsgarous.interaction.vote;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.MaireVoteOutcomeTransformer;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteModule;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcomeTransformersModule;
import com.google.inject.AbstractModule;

public final class LGInteractionVoteModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new VoteModule<>(LGPlayer.class));
        install(new VoteOutcomeTransformersModule<LGPlayer>() {
            @Override
            protected void configureVoteOutcomeTransformers() {
                addVoteOutcomeTransformer(MaireVoteOutcomeTransformer.class);
            }
        });
    }
}
