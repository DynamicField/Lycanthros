package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.cards.AnonymousNameHolder;
import com.github.jeuxjeux20.loupsgarous.game.cards.LoupGarouNightSpy;
import com.github.jeuxjeux20.loupsgarous.game.stages.LoupGarouNightKillVoteStage;
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
               orchestrator.stages().current() instanceof LoupGarouNightKillVoteStage;
    }

    @Override
    public boolean areMessagesVisibleTo(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return hasAccess(recipient, true) || isPlayerSpying(recipient);
    }

    @Override
    public boolean canTalk(LGPlayer sender, LGGameOrchestrator orchestrator) {
        return hasAccess(sender, false);
    }

    protected boolean hasAccess(LGPlayer sender, boolean canBeDead) {
        return sender.getCard().isInTeam(LGTeams.LOUPS_GAROUS) &&
               (canBeDead || sender.isAlive());
    }

    private boolean isPlayerSpying(LGPlayer recipient) {
        return recipient.getCard() instanceof LoupGarouNightSpy &&
               ((LoupGarouNightSpy) recipient.getCard()).canSpy(recipient);
    }

    @Override
    public boolean shouldAnonymizeTo(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return recipient.getCard() instanceof LoupGarouNightSpy;
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
