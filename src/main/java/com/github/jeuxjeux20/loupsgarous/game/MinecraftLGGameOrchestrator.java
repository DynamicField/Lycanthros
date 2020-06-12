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
import com.github.jeuxjeux20.loupsgarous.game.lobby.CannotCreateLobbyException;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGLobby;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGGameLobbyInfo;
import com.github.jeuxjeux20.loupsgarous.game.scoreboard.LGScoreboardManager;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStagesOrchestrator;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;
import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.*;

class MinecraftLGGameOrchestrator implements MutableLGGameOrchestrator {
    // Terminables
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();
    // Base dependencies
    private final LoupsGarous plugin;
    // Game state
    private final MutableLGGame game;
    private LGGameState state = LGGameState.UNINITIALIZED;
    // Metadata
    private final ImmutableSet<Player> initialPlayers;
    // Components
    private final LGLobby lobby;
    private final LGCardsOrchestrator cardOrchestrator;
    private final LGStagesOrchestrator stagesOrchestrator;
    private final LGChatOrchestrator chatManager;
    private final LGKillsOrchestrator killsOrchestrator;
    // UI & All
    private final LGActionBarManager actionBarManager;

    @Inject
    MinecraftLGGameOrchestrator(@Assisted LGGameLobbyInfo lobbyInfo,
                                LoupsGarous plugin,
                                LGActionBarManager actionBarManager,
                                LGScoreboardManager scoreboardManager,
                                LGInventoryManager inventoryManager,
                                LGChatOrchestrator.Factory chatManagerFactory,
                                LGBossBarManager.Factory bossBarManagerFactory,
                                LGLobby.Factory lobbyFactory,
                                LGCardsOrchestrator.Factory cardOrchestratorFactory,
                                LGStagesOrchestrator.Factory stagesOrchestratorFactory,
                                LGKillsOrchestrator.Factory killsOrchestratorFactory) throws CannotCreateLobbyException {
        this.initialPlayers = lobbyInfo.getPlayers();
        this.plugin = plugin;
        this.actionBarManager = actionBarManager;
        this.game = new MutableLGGame(lobbyInfo.getId());
        this.lobby = lobbyFactory.create(lobbyInfo, this);
        this.cardOrchestrator = cardOrchestratorFactory.create(this);
        this.stagesOrchestrator = stagesOrchestratorFactory.create(this);
        this.chatManager = chatManagerFactory.create(this);
        this.killsOrchestrator = killsOrchestratorFactory.create(this);
        LGBossBarManager bossBarManager = bossBarManagerFactory.create(this);

        bind(Schedulers.sync().runRepeating(this::updateActionBars, 20, 5));
        bindModule(bossBarManager.createUpdateModule());

        registerLobbyEvents();
        scoreboardManager.registerEvents();
        inventoryManager.registerEvents();
    }

    private void updateActionBars() {
        game().getPlayers().forEach(player -> actionBarManager.update(player, this));
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

        changeStateTo(WAITING_FOR_PLAYERS, LGGameWaitingForPlayersEvent::new);

        initialPlayers.forEach(lobby::addPlayer);

        if (game().getPlayers().isEmpty()) {
            delete(); // No online players have been added, so bye!
            return;
        }

        if (stages().current() instanceof LGStage.Null) {
            stages().next();
        }
    }

    @Override
    public void start() {
        state.mustBe(READY_TO_START);

        game.distributeCards(lobby.getComposition());
        changeStateTo(STARTED, LGGameStartEvent::new);

        callEvent(new LGTurnChangeEvent(this));

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

        game().getPlayers().stream()
                .map(LGPlayer::getPlayerUUID)
                .forEach(lobby::removePlayer);

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

        callEvent(new LGTurnChangeEvent(this));
    }

    private void updateLobbyState() {
        state.mustBe(UNINITIALIZED, WAITING_FOR_PLAYERS, READY_TO_START);

        if (lobby.isFull() && lobby.isCompositionValid()) {
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
        if (game().isEmpty() && state.isEnabled()) {
            delete();
        }
    }

    public void callEvent(LGEvent event) {
        plugin.getServer().getPluginManager().callEvent(event);
    }

    @Nonnull
    @Override
    public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", game.getId())
                .append("state", state)
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

    /**
     * Changes the current state to the specified {@code state}, and calls the event created using the given
     * function.
     *
     * @param state         the state to change to
     * @param eventFunction the function that creates the event to call
     * @param <E>           the type of the event
     * @throws IllegalStateException when the state's game type is not the same as the current one
     */
    private <E extends LGEvent> void changeStateTo(LGGameState state,
                                                   Function<? super LGGameOrchestrator, E> eventFunction) {
        if (this.state == state) return;

        this.state = state;

        callEvent(eventFunction.apply(this));
    }
}
