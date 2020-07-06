package com.github.jeuxjeux20.loupsgarous.game.chat.interceptor;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.PetiteFilleCard;
import com.github.jeuxjeux20.loupsgarous.game.chat.AnonymizedChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class PetiteFilleSpyInterceptor extends LGChatChannelInterceptor implements AnonymizedChatChannel {
    @Inject
    PetiteFilleSpyInterceptor(@Assisted LGChatChannel channel) {
        super(channel);
    }

    @Override
    public boolean isReadable(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return super.isReadable(recipient, orchestrator) || canSpy(recipient);
    }

    private boolean canSpy(LGPlayer sender) {
        return sender.getCard() instanceof PetiteFilleCard &&
               sender.isAlive();
    }

    @Override
    public boolean shouldAnonymizeTo(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return redirect(AnonymizedChatChannel.class, c -> c.shouldAnonymizeTo(recipient, orchestrator)) ||
               canSpy(recipient);
    }

    @Override
    @Redirection
    public String anonymizeName(LGPlayer player, LGGameOrchestrator orchestrator) {
        return redirect(AnonymizedChatChannel.class, c -> c.anonymizeName(player, orchestrator));
    }
}
