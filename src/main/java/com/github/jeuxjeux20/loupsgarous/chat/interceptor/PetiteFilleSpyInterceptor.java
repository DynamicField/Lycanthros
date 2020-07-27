package com.github.jeuxjeux20.loupsgarous.chat.interceptor;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.chat.AnonymizedChatChannel;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.powers.PetiteFillePower;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class PetiteFilleSpyInterceptor extends LGChatChannelInterceptor implements AnonymizedChatChannel {
    @Inject
    PetiteFilleSpyInterceptor(@Assisted LGChatChannel channel) {
        super(channel);
    }

    @Override
    public boolean isReadable(LGPlayer recipient) {
        return super.isReadable(recipient) || canSpy(recipient);
    }

    private boolean canSpy(LGPlayer sender) {
        return sender.powers().has(PetiteFillePower.class) &&
               sender.isAlive();
    }

    @Override
    public boolean shouldAnonymizeTo(LGPlayer recipient) {
        return redirect(AnonymizedChatChannel.class, c -> c.shouldAnonymizeTo(recipient)) ||
               canSpy(recipient);
    }

    @Override
    @Redirection
    public String anonymizeName(LGPlayer player) {
        return redirect(AnonymizedChatChannel.class, c -> c.anonymizeName(player));
    }
}
