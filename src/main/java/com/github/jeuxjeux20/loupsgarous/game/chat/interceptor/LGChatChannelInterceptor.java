package com.github.jeuxjeux20.loupsgarous.game.chat.interceptor;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.function.Function;

public abstract class LGChatChannelInterceptor implements LGChatChannel {
    private final LGChatChannel channel;
    protected final LGGameOrchestrator orchestrator;

    @Inject
    public LGChatChannelInterceptor(@Assisted LGChatChannel channel) {
        this.channel = channel;
        this.orchestrator = channel.gameOrchestrator();
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
    public boolean isReadable(LGPlayer recipient) {
        return channel.isReadable(recipient);
    }

    @Override
    @Redirection
    public boolean isWritable(LGPlayer sender) {
        return channel.isWritable(sender);
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return channel.gameOrchestrator();
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
