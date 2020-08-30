package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints;
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

    public final ChatChannelView getView(ChatContext context) {
        ChatChannelView view = getUnalteredView(context);
        ImmutableSet<ChatChannelViewTransformer> transformers = context.getOrchestrator().getBundle()
                .contents(LGExtensionPoints.CHANNEL_PROPERTIES_TRANSFORMERS);

        ChatChannelViewTransformer.runAll(transformers, context, view);

        return view;
    }

    public final ChatChannelView getUnalteredView(ChatContext context) {
        ChatChannelView view = new ChatChannelView(this);
        setupView(context, view);
        return view;
    }

    protected abstract void setupView(ChatContext context, ChatChannelView view);

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
