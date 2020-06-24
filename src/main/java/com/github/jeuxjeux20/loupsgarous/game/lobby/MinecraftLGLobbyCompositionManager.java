package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.SnapshotComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.gui.CompositionGui;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.CompositionValidator;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.LGGameStartEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.lobby.LGLobbyCompositionChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.lobby.LGLobbyOwnerChangeEvent;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class MinecraftLGLobbyCompositionManager implements LGLobbyCompositionManager {
    private final LGGameOrchestrator orchestrator;
    private final CompositionValidator compositionValidator;
    private final CompositionGui.Factory compositionGuiFactory;

    private final LobbyComposition composition;
    private @Nullable CompositionValidator.Problem.Type worseCompositionProblemType;

    @Inject
    MinecraftLGLobbyCompositionManager(@Assisted LGGameOrchestrator orchestrator,
                                       @Assisted LGGameBootstrapData bootstrapData,
                                       CompositionValidator compositionValidator,
                                       CompositionGui.Factory compositionGuiFactory) {
        this.orchestrator = orchestrator;
        this.compositionValidator = compositionValidator;
        this.compositionGuiFactory = compositionGuiFactory;

        this.composition = new LobbyComposition(bootstrapData.getComposition());

        updateCompositionProblemType();
    }

    @Override
    public void openOwnerGui() {
        if (orchestrator.lobby().isLocked()) return;

        Player player = orchestrator.lobby().getOwner().getMinecraftPlayer().orElseThrow(AssertionError::new);
        CompositionGui gui = compositionGuiFactory.create(player, composition);
        gui.open();

        Events.merge(LGEvent.class,
                LGGameStartEvent.class, LGGameDeletedEvent.class, LGLobbyOwnerChangeEvent.class)
                .expireIf(x -> !gui.isValid())
                .filter(x -> x.getOrchestrator() == orchestrator)
                .handler(e -> gui.close())
                .bindWith(gui);
    }

    @Override
    public Composition get() {
        return new SnapshotComposition(composition);
    }

    @Override
    public @Nullable CompositionValidator.Problem.Type getWorstProblemType() {
        return worseCompositionProblemType;
    }


    private void updateCompositionProblemType() {
        worseCompositionProblemType = compositionValidator.validate(composition).stream()
                .map(CompositionValidator.Problem::getType)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }


    private final class LobbyComposition extends MutableComposition {
        public LobbyComposition(Composition composition) {
            super(composition);
        }

        @Override
        public boolean isValidPlayerCount(int playerCount) {
            return super.isValidPlayerCount(playerCount) &&
                   orchestrator.game().getPlayers().size() <= playerCount;
        }

        @Override
        protected void onChange() {
            updateCompositionProblemType();
            Events.call(new LGLobbyCompositionChangeEvent(orchestrator));
        }
    }
}
