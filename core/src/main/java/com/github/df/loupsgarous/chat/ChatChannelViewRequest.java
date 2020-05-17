package com.github.df.loupsgarous.chat;

import com.github.df.loupsgarous.mechanic.MechanicRequest;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;

public class ChatChannelViewRequest extends MechanicRequest {
    private final LGPlayer viewer;
    private final ChatChannel chatChannel;

    public ChatChannelViewRequest(LGGameOrchestrator orchestrator,
                                  LGPlayer viewer, ChatChannel chatChannel) {
        super(orchestrator);
        this.viewer = viewer;
        this.chatChannel = chatChannel;
    }

    public LGPlayer getViewer() {
        return viewer;
    }

    public ChatChannel getChatChannel() {
        return chatChannel;
    }
}
