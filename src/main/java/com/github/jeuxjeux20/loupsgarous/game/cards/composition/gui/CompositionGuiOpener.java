package com.github.jeuxjeux20.loupsgarous.game.cards.composition.gui;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.LGGameStartEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.lobby.LGLobbyOwnerChangeEvent;
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
        MutableComposition mutable = orchestrator.lobby().composition().getMutable()
                .orElseThrow(() -> new IllegalStateException("The lobby is locked."));

        Player minecraftPlayer = orchestrator.lobby().getOwner().getMinecraftPlayer()
                .orElseThrow(() -> new IllegalStateException("There is no owner."));

        CompositionGui gui = factory.create(minecraftPlayer, mutable);

        gui.open();

        Events.merge(LGEvent.class,
                LGGameStartEvent.class, LGGameDeletedEvent.class, LGLobbyOwnerChangeEvent.class)
                .expireIf(x -> !gui.isValid())
                .filter(x -> x.getOrchestrator() == orchestrator)
                .handler(e -> gui.close())
                .bindWith(gui);
    }
}
