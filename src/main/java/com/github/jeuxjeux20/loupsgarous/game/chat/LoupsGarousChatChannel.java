package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.google.inject.Inject;

public class LoupsGarousChatChannel extends AbstractLGChatChannel implements AnonymizedChatChannel {
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
    LoupsGarousChatChannel(LGGameOrchestrator orchestrator,
                           AnonymizedNamesProvider.Factory anonymizedNamesProviderFactory) {
        super(orchestrator);
        this.anonymizedNamesProvider = anonymizedNamesProviderFactory.create(ANONYMIZED_NAMES);
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
    public boolean isReadable(LGPlayer recipient) {
        return hasAccess(recipient);
    }

    @Override
    public boolean isWritable(LGPlayer sender) {
        return hasAccess(sender);
    }

    protected boolean hasAccess(LGPlayer sender) {
        return sender.teams().has(LGTeams.LOUPS_GAROUS);
    }

    @Override
    public boolean shouldAnonymizeTo(LGPlayer recipient) {
        return false;
    }

    @Override
    public String anonymizeName(LGPlayer player) {
        return anonymizedNamesProvider.updateAnonymousNameOrGet(player);
    }
}
