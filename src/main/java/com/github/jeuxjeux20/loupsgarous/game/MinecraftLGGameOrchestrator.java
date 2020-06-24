package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.actionbar.LGActionBarManager;
import com.github.jeuxjeux20.loupsgarous.game.bossbar.LGBossBarManager;
import com.github.jeuxjeux20.loupsgarous.game.cards.LGCardsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.event.*;
import com.github.jeuxjeux20.loupsgarous.game.event.lobby.LGLobbyCompositionChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.game.inventory.LGInventoryManager;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKillsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.PlayerQuitKillReason;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGGameBootstrapData;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGLobby;
import com.github.jeuxjeux20.loupsgarous.game.scoreboard.LGScoreboardManager;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStagesOrchestrator;
import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import me.lucko.helper.metadata.MetadataMap;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.logging.Logger;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;
import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.*;

class MinecraftLGGameOrchestrator implements MutableLGGameOrchestrator {
    // Terminables
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();
    // Base dependencies
    private final LoupsGarous plugin;
    private final LGGameOrchestratorLogger logger;
    // Game state
    private final MutableLGGame game;
    private LGGameState state = LGGameState.UNINITIALIZED;
    // Components
    private final LGLobby lobby;
    private final LGCardsOrchestrator cardOrchestrator;
    private final LGStagesOrchestrator stagesOrchestrator;
    private final LGChatOrchestrator chatManager;
    private final LGKillsOrchestrator killsOrchestrator;

    @Inject
    MinecraftLGGameOrchestrator(@Assisted LGGameBootstrapData bootstrapData,
                                LoupsGarous plugin,
                                LGScoreboardManager scoreboardManager,
                                LGInventoryManager inventoryManager,
                                LGChatOrchestrator.Factory chatManagerFactory,
                                LGBossBarManager.Factory bossBarManagerFactory,
                                LGActionBarManager.Factory actionBarManagerFactory,
                                LGLobby.Factory lobbyFactory,
                                LGCardsOrchestrator.Factory cardOrchestratorFactory,
                                LGStagesOrchestrator.Factory stagesOrchestratorFactory,
                                LGKillsOrchestrator.Factory killsOrchestratorFactory) throws GameCreationException {
        try {
            this.plugin = plugin;
            this.game = new MutableLGGame(bootstrapData.getId());
            this.logger = new LGGameOrchestratorLogger(this);

            this.lobby = lobbyFactory.create(bootstrapData, this);
            this.cardOrchestrator = cardOrchestratorFactory.create(this);
            this.stagesOrchestrator = stagesOrchestratorFactory.create(this);
            this.chatManager = chatManagerFactory.create(this);
            this.killsOrchestrator = killsOrchestratorFactory.create(this);
            LGBossBarManager bossBarManager = bossBarManagerFactory.create(this);
            LGActionBarManager actionBarManager = actionBarManagerFactory.create(this);

            bindModule(actionBarManager.createUpdateModule());
            bindModule(bossBarManager.createUpdateModule());

            registerLobbyEvents();
            scoreboardManager.registerEvents();
            inventoryManager.registerEvents();
        } catch (Exception e) {
            // Ensure that all the terminables get closed
            // before rethrowing.
            terminableRegistry.closeAndReportException();

            throw e;
        }
    }

    @Override
    public MutableLGGame game() {
        return game;
    }

    @Override
    public LGGameState state() {
        return state;
    }

    @Override
    public void initialize() {
        state.mustBe(UNINITIALIZED);

        Events.call(new LGGameInitializeEvent(this));

        updateLobbyState();

        if (stages().current() instanceof LGStage.Null) {
            stages().next();
        }
    }

    @Override
    public void start() {
        state.mustBe(READY_TO_START);

        game.distributeCards(lobby.composition().get());

        changeStateTo(STARTED, LGGameStartEvent::new);

        Events.call(new LGTurnChangeEvent(this));

        stages().next();
    }

    @Override
    public void finish(LGEnding ending) {
        // A game can be finished at any state except when it's already finished or deleted.
        state.mustNotBe(FINISHED, DELETING, DELETED);

        game.setEnding(ending);

        changeStateTo(FINISHED, o -> new LGGameFinishedEvent(o, ending));

        stages().next();
    }

    @Override
    public void delete() {
        state.mustNotBe(DELETING, DELETED);

        changeStateTo(DELETING, LGGameDeletingEvent::new);

        terminableRegistry.closeAndReportException();
        game.getPlayers().forEach(lobby::removePlayer);

        changeStateTo(DELETED, LGGameDeletedEvent::new);
    }

    @Override
    public void nextTimeOfDay() {
        state.mustBe(STARTED);

        MutableLGGameTurn turn = game.getTurn();
        if (turn.getTime() == LGGameTurnTime.DAY) {
            turn.setTurnNumber(turn.getTurnNumber() + 1);
            turn.setTime(LGGameTurnTime.NIGHT);
        } else {
            turn.setTime(LGGameTurnTime.DAY);
        }

        Events.call(new LGTurnChangeEvent(this));
    }

    private void deleteIfEmpty() {
        if (game().isEmpty()) {
            delete();
        }
    }

    private void updateLobbyState() {
        state.mustBe(UNINITIALIZED, WAITING_FOR_PLAYERS, READY_TO_START);

        if (lobby.isFull() && lobby.composition().isValid()) {
            changeStateTo(READY_TO_START, LGGameReadyToStartEvent::new);
        } else {
            changeStateTo(WAITING_FOR_PLAYERS, LGGameWaitingForPlayersEvent::new);
        }
    }

    private void registerLobbyEvents() {
        Events.subscribe(LGPlayerQuitEvent.class)
                .filter(this::isMyEvent)
                .handler(this::handlePlayerQuit)
                .bindWith(this);

        Events.subscribe(LGPlayerJoinEvent.class)
                .filter(this::isMyEvent)
                .handler(this::handlePlayerJoin)
                .bindWith(this);

        Events.merge(LGEvent.class, LGPlayerJoinEvent.class, LGPlayerQuitEvent.class, LGLobbyCompositionChangeEvent.class)
                .filter(this::isMyEvent)
                .filter(o -> !lobby.isLocked() && state != LGGameState.UNINITIALIZED)
                .handler(e -> updateLobbyState())
                .bindWith(this);
    }

    private void handlePlayerJoin(LGPlayerJoinEvent event) {
        chat().sendToEveryone(player(event.getPlayer().getName()) + lobbyMessage(" a rejoint la partie ! ") +
                              slots(lobby.getSlotsDisplay()));
    }

    private void handlePlayerQuit(LGPlayerQuitEvent e) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(e.getPlayerUUID());

        if (isGameRunning() && e.getLGPlayer().isAlive()) {
            kills().instantly(e.getLGPlayer(), PlayerQuitKillReason::new);
        } else if (state.isEnabled()) { // Let's not write quit messages while deleting.
            chat().sendToEveryone(player(offlinePlayer.getName()) + lobbyMessage(" a quitté la partie ! ") +
                                  slots(lobby.getSlotsDisplay()));
        }

        // Are they all gone?
        deleteIfEmpty();
    }

    @Nonnull
    @Override
    public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", game.getId())
                .add("state", state)
                .toString();
    }

    @Override
    public LoupsGarous plugin() {
        return plugin;
    }

    @Override
    public LGChatOrchestrator chat() {
        return chatManager;
    }

    @Override
    public LGStagesOrchestrator stages() {
        return stagesOrchestrator;
    }

    @Override
    public LGCardsOrchestrator cards() {
        return cardOrchestrator;
    }

    @Override
    public LGKillsOrchestrator kills() {
        return killsOrchestrator;
    }

    @Override
    public LGLobby lobby() {
        return lobby;
    }

    @Override
    public MetadataMap metadata() {
        return LGMetadata.provideForGame(this);
    }

    @Override
    public Logger logger() {
        return logger;
    }

    /**
     * Changes the current state to the specified {@code state}, and calls the event created using the given
     * function.
     *
     * @param state         the state to change to
     * @param eventFunction the function that creates the event to call
     * @throws IllegalStateException when the state's game type is not the same as the current one
     */
    private void changeStateTo(LGGameState state,
                               Function<? super LGGameOrchestrator, ? extends LGEvent> eventFunction) {
        if (this.state == state) return;

        LGGameState oldState = this.state;
        this.state = state;

        logger.fine("State changed: " + oldState + " -> " + state);

        Events.call(eventFunction.apply(this));
    }
}
