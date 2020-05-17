package com.github.df.loupsgarous.chat;

import com.github.df.loupsgarous.mechanic.Mechanic;
import com.github.df.loupsgarous.game.LGPlayer;
import com.google.common.base.MoreObjects;

import java.util.Objects;

public abstract class ChatChannel {
    public static final Mechanic<ChatChannelViewRequest, ChatChannelView> VIEW_MECHANIC =
            new Mechanic<ChatChannelViewRequest, ChatChannelView>() {
                @Override
                protected ChatChannelView serve(ChatChannelViewRequest request) {
                    ChatChannelView view
                            = new ChatChannelView(request.getChatChannel(), request.getViewer());

                    request.getChatChannel().setupView(view);

                    return view;
                }
            };

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
        return VIEW_MECHANIC.get(
                new ChatChannelViewRequest(player.getOrchestrator(), player, this));
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
