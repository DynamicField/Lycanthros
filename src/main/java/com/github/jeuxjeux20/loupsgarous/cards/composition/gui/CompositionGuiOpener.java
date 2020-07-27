package com.github.jeuxjeux20.loupsgarous.cards.composition.gui;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGGameStartEvent;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGLobbyCompositionUpdateEvent;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGLobbyOwnerChangeEvent;
import com.google.inject.Inject;
import me.lucko.helper.Events;
import org.bukkit.entity.Player;

public class CompositionGuiOpener {
    private final CompositionGui.Factory factory;

    @Inject
    CompositionGuiOpener(CompositionGui.Factory factory) {
        this.factory = factory;
    }

    public void open(LGGameOrchestrator orchestrator) {
        if (orchestrator.lobby().isLocked()) {
            throw new IllegalStateException("The lobby is locked.");
        }

        Player minecraftPlayer = orchestrator.lobby().getOwner().minecraft()
                .orElseThrow(AssertionError::new);

        CompositionGui gui = factory.create(minecraftPlayer,
                orchestrator.lobby().composition()::get,
                orchestrator.lobby().composition()::update);

        gui.open();

        Events.merge(LGEvent.class,
                LGGameStartEvent.class, LGGameDeletedEvent.class, LGLobbyOwnerChangeEvent.class)
                .filter(e -> e.getOrchestrator() == orchestrator)
                .handler(e -> gui.close())
                .bindWith(gui);

        Events.subscribe(LGLobbyCompositionUpdateEvent.class)
                .filter(e -> e.getOrchestrator() == orchestrator)
                .handler(e -> gui.redraw())
                .bindWith(gui);
    }
}
