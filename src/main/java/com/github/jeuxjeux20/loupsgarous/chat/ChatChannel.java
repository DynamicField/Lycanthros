package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

import java.util.Objects;

public abstract class ChatChannel {
    private final String id;
    private final String defaultName;

    public ChatChannel(String id, String defaultName) {
        this.id = id;
        this.defaultName = defaultName;
    }

    public String getId() {
        return id;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public final ChatChannelView getView(LGPlayer player) {
        ChatChannelView view = new ChatChannelView(this, player);

        setupView(view);

        ImmutableSet<ChatChannelViewMechanic> mechanics = view.getOrchestrator().getGameBox()
                .contents(LGExtensionPoints.CHAT_CHANNEL_VIEW_MECHANICS);

        for (ChatChannelViewMechanic mechanic : mechanics) {
            if (mechanic.handlesChannel(this)) {
                mechanic.execute(view);
            }
        }

        return view;
    }

    protected abstract void setupView(ChatChannelView view);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatChannel that = (ChatChannel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("defaultName", defaultName)
                .toString();
    }
}
