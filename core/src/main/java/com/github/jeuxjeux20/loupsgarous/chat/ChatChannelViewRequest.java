package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.mechanic.MechanicRequest;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

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
