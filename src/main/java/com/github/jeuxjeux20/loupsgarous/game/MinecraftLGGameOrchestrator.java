package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.ReactiveProperty;
import com.github.jeuxjeux20.loupsgarous.ReactiveValue;
import com.github.jeuxjeux20.loupsgarous.cards.distribution.CardDistributor;
import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.event.*;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGLobbyCompositionUpdateEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.extensibility.GameBundle;
import com.github.jeuxjeux20.loupsgarous.extensibility.ModBundle;
import com.github.jeuxjeux20.loupsgarous.extensibility.ModRegistry;
import com.github.jeuxjeux20.loupsgarous.kill.causes.PlayerQuitKillCause;
import com.github.jeuxjeux20.loupsgarous.lobby.LGGameBootstrapData;
import com.github.jeuxjeux20.loupsgarous.lobby.LGLobby;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhase;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import io.reactivex.rxjava3.disposables.Disposable;
import me.lucko.helper.Events;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.metadata.MetadataMap;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.logging.Logger;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;
import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.*;

class MinecraftLGGameOrchestrator implements InternalLGGameOrchestrator {
    // Terminables
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();
    private final Injector injector;
    // Base dependencies
    private final LoupsGarous plugin;
    private final LGGameOrchestratorLogger logger;
    // Game state
    private final OrchestratedLGGame game;
    private final ModRegistry modRegistry;
    // Components
    private final LGLobby lobby;
    private final CardDistributor cardDistributor;
    private DelayedDependencies delayedDependencies;
    private final Provider<DelayedDependencies> delayedDependenciesProvider;
    private final OrchestratorScope scope;

    private final ReactiveProperty<GameBundle> bundle = new ReactiveProperty<>();

    @Inject
    MinecraftLGGameOrchestrator(@Assisted LGGameBootstrapData bootstrapData,
                                Injector injector,
                                LoupsGarous plugin,
                                LGLobby.Factory lobbyFactory,
                                CardDistributor cardDistributor,
                                OrchestratorScope scope,
                                ModRegistry modRegistry,
                                Provider<DelayedDependencies> delayedDependenciesProvider)
            throws GameCreationException {
        this.injector = injector;
        this.plugin = plugin;
        this.cardDistributor = cardDistributor;
        this.scope = scope;
        this.game = new OrchestratedLGGame(bootstrapData.getId(), modRegistry.createDefaultBundle());
        this.modRegistry = modRegistry;
        this.logger = new LGGameOrchestratorLogger(this);
        this.delayedDependenciesProvider = delayedDependenciesProvider;

        this.lobby = lobbyFactory.create(bootstrapData, this);

        registerLobbyEvents();
    }


    @Override
    public OrchestratedLGGame game() {
        return game;
    }

    @Override
    public void initialize() {
        state().mustBe(UNINITIALIZED);

        try (OrchestratorScope.Block ignored = scope()) {
            delayedDependencies = delayedDependenciesProvider.get();
        }

        bind(delayedDependencies);

        updateLobbyState();

        if (phases().current() instanceof LGPhase.Null) {
            phases().next();
        }

        Events.call(new LGGameInitializedEvent(this));
    }

    @Override
    public void start() {
        state().mustBe(READY_TO_START);

        game.distributeCards(cardDistributor, lobby.composition().get());

        changeStateTo(STARTED, LGGameStartEvent::new);

        Events.call(new LGTurnChangeEvent(this));

        phases().next();
    }

    @Override
    public void finish(LGEnding ending) {
        state().mustNotBe(UNINITIALIZED, FINISHED, DELETING, DELETED);

        game.setEnding(ending);

        changeStateTo(FINISHED, o -> new LGGameFinishedEvent(o, ending));

        phases().next();
    }

    @Override
    public void delete() {
        state().mustNotBe(DELETING, DELETED);

        changeStateTo(DELETING, LGGameDeletingEvent::new);

        game.getPlayers().forEach(lobby::leave);
        terminableRegistry.closeAndReportException();

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

    @Override
    public <T> T create(Class<T> clazz) {
        try (OrchestratorScope.Block ignored = scope()) {
            return injector.getInstance(clazz);
        }
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
        bind(Disposable.toAutoCloseable(
                lobby.mods().observe().subscribe(this::updateBundle)
        ));

        Events.subscribe(LGPlayerQuitEvent.class, EventPriority.MONITOR)
                .filter(this::isMyEvent)
                .filter(e -> state().isEnabled())
                .handler(this::handlePlayerQuit)
                .bindWith(this);

        Events.subscribe(LGPlayerJoinEvent.class, EventPriority.MONITOR)
                .filter(this::isMyEvent)
                .filter(e -> state().isEnabled())
                .handler(this::handlePlayerJoin)
                .bindWith(this);

        Events.merge(LGEvent.class,
                LGPlayerJoinEvent.class, LGPlayerQuitEvent.class, LGLobbyCompositionUpdateEvent.class)
                .filter(this::isMyEvent)
                .filter(o -> !lobby.isLocked() && state() != UNINITIALIZED)
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
        }

        if (!lobby.isLocked()) {
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
    public GameBundle bundle() {
        // Very crappy solution for now
        if (bundle.get() == null) {
            updateBundle(game.getMods());
        }
        return bundle.get();
    }

    @Override
    public ReactiveValue<GameBundle> reactiveBundle() {
        return bundle;
    }

    private GameBundle createBundle(ModBundle modBundle) {
        return new GameBundle(modBundle.createEnabledModsExtensions(), this::create);
    }

    @Override
    public <T extends OrchestratorComponent> T component(MetadataKey<T> key) {
        checkDelayedDependencies();

        return delayedDependencies.componentMap.get(key).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public LGLobby lobby() {
        return lobby;
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
     * Changes the current state to the specified {@code state}, and calls the event created using
     * the given function.
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

    private void updateBundle(ModBundle newBundle) {
        bundle.set(createBundle(newBundle));
    }

    @OrchestratorScoped
    private static final class DelayedDependencies implements Terminable {
        final MetadataMap componentMap = MetadataMap.create();

        @Inject
        DelayedDependencies(Map<MetadataKey<?>, OrchestratorComponent> rawComponentMap) {
            rawComponentMap.forEach(this::addToComponentMap);
        }

        @SuppressWarnings("unchecked")
        private <T extends OrchestratorComponent>
        void addToComponentMap(MetadataKey<?> key, T entry) {
            this.componentMap.put((MetadataKey<? super T>) key, entry);
        }

        @Override
        public void close() throws CompositeClosingException {
            CompositeTerminable terminables = CompositeTerminable.create();

            for (Object value : componentMap.asMap().values()) {
                terminables.bind(((OrchestratorComponent) value));
            }

            terminables.close();
        }
    }
}
