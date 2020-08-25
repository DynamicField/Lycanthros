package com.github.jeuxjeux20.loupsgarous.cards.composition.gui;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGGameStartEvent;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGLobbyCompositionUpdateEvent;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGLobbyOwnerChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
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
        if (orchestrator.isLocked()) {
            throw new IllegalStateException("The lobby is locked.");
        }

        LGPlayer owner = orchestrator.getOwner();
        if (owner == null) {
            throw new IllegalStateException("Cannot open the composition gui when the game has no owner.");
        }

        Player minecraftOwner = owner.minecraft().orElseThrow(AssertionError::new);

        CompositionGui gui = factory.create(minecraftOwner, orchestrator);

        gui.open();

        Events.merge(LGEvent.class,
                LGGameStartEvent.class, LGGameDeletedEvent.class, LGLobbyOwnerChangeEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> gui.close())
                .bindWith(gui);

        Events.subscribe(LGLobbyCompositionUpdateEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> gui.redraw())
                .bindWith(gui);
    }
}
