package com.github.df.loupsgarous.gui;

import com.github.df.loupsgarous.event.LGEvent;
import com.github.df.loupsgarous.event.LGGameStartEvent;
import com.github.df.loupsgarous.event.lobby.LGOwnerChangeEvent;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.menu.Gui;
import org.bukkit.entity.Player;

public abstract class OwnerGui extends Gui {
    protected final LGGameOrchestrator orchestrator;

    public OwnerGui(Player player, int lines, String title, LGGameOrchestrator orchestrator) {
        super(player, lines, title);
        this.orchestrator = orchestrator;
    }

    @Override
    public void open() {
        if (!orchestrator.allowsJoin()) {
            throw new IllegalStateException("The orchestrator is not in a LOBBY phase.");
        }

        LGPlayer player = orchestrator.getPlayer(getPlayer().getUniqueId()).orElse(null);
        if (player != orchestrator.getOwner()) {
            throw new IllegalStateException("This GUI's player is not the owner.");
        }

        super.open();

        listenToEvents();
    }

    private void listenToEvents() {
        Events.merge(LGEvent.class, LGGameStartEvent.class, LGOwnerChangeEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> close())
                .bindWith(this);
    }
}
