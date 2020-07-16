package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.actionbar.LGActionBarManager;
import com.github.jeuxjeux20.loupsgarous.game.bossbar.LGBossBarManager;
import com.github.jeuxjeux20.loupsgarous.game.cards.distribution.CardDistributor;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.event.*;
import com.github.jeuxjeux20.loupsgarous.game.event.lobby.LGLobbyCompositionChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableRegistry;
import com.github.jeuxjeux20.loupsgarous.game.inventory.LGInventoryManager;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKillsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.kill.causes.PlayerQuitKillCause;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGGameBootstrapData;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGLobby;
import com.github.jeuxjeux20.loupsgarous.game.scoreboard.LGScoreboardManager;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStagesOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTagsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeamsOrchestrator;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

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
    // Components
    private final LGLobby lobby;
    private final CardDistributor cardDistributor;
    private DelayedDependencies delayedDependencies;
    private final Provider<DelayedDependencies> delayedDependenciesProvider;
    private final OrchestratorScope scope;

    @Inject
    MinecraftLGGameOrchestrator(@Assisted LGGameBootstrapData bootstrapData,
                                LoupsGarous plugin,
                                LGLobby.Factory lobbyFactory,
                                CardDistributor cardDistributor,
                                OrchestratorScope scope,
                                Provider<DelayedDependencies> delayedDependenciesProvider) throws GameCreationException {
        this.plugin = plugin;
        this.cardDistributor = cardDistributor;
        this.scope = scope;
        this.game = new MutableLGGame(bootstrapData.getId());
        this.logger = new LGGameOrchestratorLogger(this);
        this.delayedDependenciesProvider = delayedDependenciesProvider;

        this.lobby = lobbyFactory.create(bootstrapData, this);

        registerLobbyEvents();
    }

    @Override
    public MutableLGGame game() {
        return game;
    }

    @Override
    public void initialize() {
        state().mustBe(UNINITIALIZED);

        try (OrchestratorScope.Block block = scope()) {
            delayedDependencies = delayedDependenciesProvider.get();
        }

        updateLobbyState();

        if (stages().current() instanceof LGStage.Null) {
            stages().next();
        }

        Events.call(new LGGameInitializedEvent(this));
    }

    @Override
    public void start() {
        state().mustBe(READY_TO_START);

        game.distributeCards(cardDistributor, lobby.composition().get());

        changeStateTo(STARTED, LGGameStartEvent::new);

        Events.call(new LGTurnChangeEvent(this));

        stages().next();
    }

    @Override
    public void finish(LGEnding ending) {
        state().mustNotBe(UNINITIALIZED, FINISHED, DELETING, DELETED);

        game.setEnding(ending);

        changeStateTo(FINISHED, o -> new LGGameFinishedEvent(o, ending));

        stages().next();
    }

    @Override
    public void delete() {
        state().mustNotBe(DELETING, DELETED);

        changeStateTo(DELETING, LGGameDeletingEvent::new);

        terminableRegistry.closeAndReportException();
        game.getPlayers().forEach(lobby::removePlayer);

        changeStateTo(DELETED, LGGameDeletedEvent::new);
    }

    @Override
    public void nextTimeOfDay() {
        state().mustBe(STARTED);

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
        state().mustBe(UNINITIALIZED, WAITING_FOR_PLAYERS, READY_TO_START);

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
                .filter(o -> !lobby.isLocked() && state() != LGGameState.UNINITIALIZED)
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
            kills().instantly(e.getLGPlayer(), PlayerQuitKillCause.INSTANCE);
        } else if (state().isEnabled()) { // Let's not write quit messages while deleting.
            chat().sendToEveryone(player(offlinePlayer.getName()) + lobbyMessage(" a quitté la partie ! ") +
                                  slots(lobby.getSlotsDisplay()));
        }

        // Are they all gone?
        deleteIfEmpty();
    }

    @Override
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public LGChatOrchestrator chat() {
        checkDelayedDependencies();

        return delayedDependencies.chatOrchestrator;
    }

    @Override
    public LGStagesOrchestrator stages() {
        checkDelayedDependencies();

        return delayedDependencies.stagesOrchestrator;
    }

    @Override
    public LGTeamsOrchestrator teams() {
        checkDelayedDependencies();

        return delayedDependencies.teamsOrchestrator;
    }

    @Override
    public LGTagsOrchestrator tags() {
        checkDelayedDependencies();

        return delayedDependencies.tagsOrchestrator;
    }

    @Override
    public LGKillsOrchestrator kills() {
        checkDelayedDependencies();

        return delayedDependencies.killsOrchestrator;
    }

    @Override
    public LGLobby lobby() {
        return lobby;
    }

    @Override
    public InteractableRegistry interactables() {
        checkDelayedDependencies();

        return delayedDependencies.interactableRegistry;
    }

    @Override
    public LGActionBarManager actionBar() {
        checkDelayedDependencies();

        return delayedDependencies.actionBarManager;
    }

    @Override
    public LGBossBarManager bossBar() {
        checkDelayedDependencies();

        return delayedDependencies.bossBarManager;
    }

    @Override
    public Logger logger() {
        return logger;
    }

    @Override
    public OrchestratorScope.Block scope() {
        return scope.use(this);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", game.getId())
                .add("state", state())
                .toString();
    }

    @Nonnull
    @Override
    public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
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
        if (this.state() == state) return;

        LGGameState oldState = this.state();
        game.setState(state);

        logger.fine("State changed: " + oldState + " -> " + state);

        Events.call(eventFunction.apply(this));
    }

    private void checkDelayedDependencies() {
        Preconditions.checkState(delayedDependencies != null,
                "This game is not initialized yet. (OrchestratorScoped dependencies are not present.)");
    }

    @OrchestratorScoped
    private static final class DelayedDependencies {
        final LGScoreboardManager scoreboardManager;
        final LGInventoryManager inventoryManager;
        final LGChatOrchestrator chatOrchestrator;
        final LGBossBarManager bossBarManager;
        final LGActionBarManager actionBarManager;
        final LGTeamsOrchestrator teamsOrchestrator;
        final LGTagsOrchestrator tagsOrchestrator;
        final LGStagesOrchestrator stagesOrchestrator;
        final LGKillsOrchestrator killsOrchestrator;
        final InteractableRegistry interactableRegistry;

        @Inject
        DelayedDependencies(LGScoreboardManager scoreboardManager,
                            LGInventoryManager inventoryManager,
                            LGChatOrchestrator chatOrchestrator,
                            LGBossBarManager bossBarManager,
                            LGActionBarManager actionBarManager,
                            LGTeamsOrchestrator teamsOrchestrator,
                            LGTagsOrchestrator tagsOrchestrator,
                            LGStagesOrchestrator stagesOrchestrator,
                            LGKillsOrchestrator killsOrchestrator,
                            InteractableRegistry interactableRegistry) {
            this.scoreboardManager = scoreboardManager;
            this.inventoryManager = inventoryManager;
            this.chatOrchestrator = chatOrchestrator;
            this.bossBarManager = bossBarManager;
            this.actionBarManager = actionBarManager;
            this.teamsOrchestrator = teamsOrchestrator;
            this.tagsOrchestrator = tagsOrchestrator;
            this.stagesOrchestrator = stagesOrchestrator;
            this.killsOrchestrator = killsOrchestrator;
            this.interactableRegistry = interactableRegistry;
        }
    }
}
