package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.cards.AnonymousNameHolder;
import com.github.jeuxjeux20.loupsgarous.game.cards.LoupGarouNightSpy;
import com.github.jeuxjeux20.loupsgarous.game.stages.LoupGarouNightKillVoteStage;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LoupsGarousChatChannel implements LGGameChatChannel, AnonymizedChatChannel {
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
            "Loup dominant"
    };

    private final AnonymizedNamesProvider anonymizedNamesProvider;

    @Inject
    public LoupsGarousChatChannel(AnonymizedNamesProvider anonymizedNamesProvider) {
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
               orchestrator.getCurrentStage() instanceof LoupGarouNightKillVoteStage;
    }

    @Override
    public boolean areMessagesVisibleTo(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return canTalk(recipient, orchestrator) || isPlayerSpying(recipient);
    }

    @Override
    public boolean canTalk(LGPlayer sender, LGGameOrchestrator orchestrator) {
        return sender.getCard().getTeams().contains(LGTeams.LOUPS_GAROUS);
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
