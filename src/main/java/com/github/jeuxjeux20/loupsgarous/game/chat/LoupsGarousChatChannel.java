package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.AnonymousNameHolder;
import com.github.jeuxjeux20.loupsgarous.game.stages.LoupGarouVoteStage;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LoupsGarousChatChannel implements LGChatChannel, AnonymizedChatChannel {
    public static final String[] ANONYMIZED_NAMES = {
            "Loup sympa",
            "Loup bizarre",
            "Loup qui adore int",
            "Loup normal",
            "Loup blanc",
            "Loup noir",
            "Loup arc-en-ciel",
            "Loup terrifiant",
            "Loup éco+",
            "Loup gris",
            "Loup dominant",
            "Loup allemand",
            "Loup speedrunner",
            "Mario LOUPdyssey"
    };

    private final AnonymizedNamesProvider anonymizedNamesProvider;

    @Inject
    LoupsGarousChatChannel(AnonymizedNamesProvider anonymizedNamesProvider) {
        this.anonymizedNamesProvider = anonymizedNamesProvider;
    }

    @Override
    public String getName() {
        return "Loups-Garous";
    }

    @Override
    public boolean isNameDisplayed() {
        return true;
    }

    @Override
    public boolean canBeUsedByPlayer(LGGameOrchestrator orchestrator) {
        return orchestrator.isGameRunning() &&
               orchestrator.stages().current() instanceof LoupGarouVoteStage;
    }

    @Override
    public boolean areMessagesVisibleTo(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return hasAccess(recipient);
    }

    @Override
    public boolean canTalk(LGPlayer sender, LGGameOrchestrator orchestrator) {
        return hasAccess(sender);
    }

    protected boolean hasAccess(LGPlayer sender) {
        return sender.getCard().isInTeam(LGTeams.LOUPS_GAROUS);
    }

    @Override
    public boolean shouldAnonymizeTo(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return false;
    }

    @Override
    public String anonymizeName(LGPlayer player, LGGameOrchestrator orchestrator) {
        if (!(player.getCard() instanceof AnonymousNameHolder))
            throw new IllegalArgumentException(
                    "The player's card (" + player.getCard().getClass().getName() + ") is not an AnonymousNameHolder.");

        AnonymousNameHolder anonymousNameHolder = (AnonymousNameHolder) player.getCard();

        return anonymizedNamesProvider.createAnonymousNameOrGet(orchestrator, anonymousNameHolder, this, ANONYMIZED_NAMES);
    }
}
