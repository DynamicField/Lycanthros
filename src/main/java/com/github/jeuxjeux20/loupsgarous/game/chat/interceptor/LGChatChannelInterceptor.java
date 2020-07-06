package com.github.jeuxjeux20.loupsgarous.game.chat.interceptor;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.function.Function;

public abstract class LGChatChannelInterceptor implements LGChatChannel {
    private final LGChatChannel channel;

    @Inject
    public LGChatChannelInterceptor(@Assisted LGChatChannel channel) {
        this.channel = channel;
    }

    @Override
    @Redirection
    public String getName() {
        return channel.getName();
    }

    @Override
    @Redirection
    public boolean isNameDisplayed() {
        return channel.isNameDisplayed();
    }

    @Override
    @Redirection
    public boolean canBeUsedByPlayer(LGGameOrchestrator orchestrator) {
        return channel.canBeUsedByPlayer(orchestrator);
    }

    @Override
    @Redirection
    public boolean areMessagesVisibleTo(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return channel.areMessagesVisibleTo(recipient, orchestrator);
    }

    @Override
    @Redirection
    public boolean canTalk(LGPlayer sender, LGGameOrchestrator orchestrator) {
        return channel.canTalk(sender, orchestrator);
    }

    protected LGChatChannel getChannel() {
        return channel;
    }

    protected <R, T> R redirect(Class<T> targetClass, Function<? super T, ? extends R> resultFunction) {
        if (targetClass.isInstance(channel)) {
            return resultFunction.apply(targetClass.cast(channel));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    interface Factory {
        LGChatChannelInterceptor create(LGChatChannel channel);
    }
}
