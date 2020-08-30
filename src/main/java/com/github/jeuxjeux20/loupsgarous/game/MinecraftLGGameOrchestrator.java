package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.ReactiveProperty;
import com.github.jeuxjeux20.loupsgarous.ReactiveValue;
import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.cards.VillageoisCard;
import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.cards.composition.ImmutableComposition;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.CompositionValidator;
import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.event.*;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGLobbyCompositionUpdateEvent;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGLobbyOwnerChangeEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.extensibility.GameBundle;
import com.github.jeuxjeux20.loupsgarous.extensibility.ModBundle;
import com.github.jeuxjeux20.loupsgarous.extensibility.ModRegistry;
import com.github.jeuxjeux20.loupsgarous.kill.causes.PlayerQuitKillCause;
import com.github.jeuxjeux20.loupsgarous.lobby.*;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhase;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import io.reactivex.rxjava3.core.Observable;
import me.lucko.helper.Events;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.metadata.MetadataMap;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;
import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.CARDS;
import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.COMPOSITION_VALIDATORS;
import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.*;

class MinecraftLGGameOrchestrator implements LGGameOrchestrator {
    // Terminables
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();
    // Base dependencies
    private final Injector injector;
    private final LoupsGarous plugin;
    private final OrchestratorLogger logger;
    private final LGGameManager gameManager;
    private final LobbyTeleporter lobbyTeleporter;
    private final OrchestratorScope scope;
    // Game state
    private final LGGameData gameData;
    private final ReactiveProperty<GameBundle> bundle = new ReactiveProperty<>();
    private ImmutableComposition composition;
    private final ReactiveValue<ModBundle> modBundle = new ReactiveValue<ModBundle>() {
        @Override
        public ModBundle get() {
            return gameData.getMods();
        }

        @Override
        protected void setNewValue(ModBundle value) {
            if (!allowsJoin()) {
                throw new IllegalStateException("The game is locked.");
            }
            gameData.setMods(value);
            updateBundle(value);
        }
    };
    private @Nullable CompositionValidator.Problem.Type worseCompositionProblemType;
    // Components
    private DelayedDependencies delayedDependencies;
    private final Provider<DelayedDependencies> delayedDependenciesProvider;
    // Other stuff
    private final List<Runnable> postInitializationActions = new ArrayList<>();

    @Inject
    MinecraftLGGameOrchestrator(@Assisted LGGameBootstrapData bootstrapData,
                                Injector injector,
                                LoupsGarous plugin,
                                LobbyTeleporter.Factory lobbyTeleporterFactory,
                                LGGameManager gameManager,
                                OrchestratorScope scope,
                                ModRegistry modRegistry,
                                Provider<DelayedDependencies> delayedDependenciesProvider)
            throws GameCreationException {
        try {
            this.injector = injector;
            this.gameManager = gameManager;
            this.lobbyTeleporter = bind(lobbyTeleporterFactory.create());
            this.plugin = plugin;
            this.scope = scope;
            this.delayedDependenciesProvider = delayedDependenciesProvider;
            this.logger = new OrchestratorLogger(bootstrapData.getId());
            this.gameData = new LGGameData(bootstrapData.getId(), modRegistry.createDefaultBundle());

            setComposition(new ImmutableComposition(bootstrapData.getComposition()));
            updateBundle(getModBundle());

            try {
                setOwner(join(bootstrapData.getOwner()));
            } catch (PlayerJoinException e) {
                throw new InvalidOwnerException(e);
            }

            registerEventListeners();
        } catch (Throwable e) {
            terminableRegistry.closeAndReportException();
            throw e;
        }
    }

    @Override
    public void initialize() {
        this.getState().mustBe(UNINITIALIZED);

        try (OrchestratorScope.Block ignored = scope.use(this)) {
            delayedDependencies = delayedDependenciesProvider.get();
        }
        bind(delayedDependencies);

        postInitializationActions.forEach(Runnable::run);
        postInitializationActions.clear();

        if (phases().current() instanceof LGPhase.Null) {
            phases().next();
        }
    }

    @Override
    public void start() {
        getState().mustBe(READY_TO_START);

        gameData.distributeCards(composition);

        changeStateTo(STARTED, LGGameStartEvent::new);

        Events.call(new LGTurnChangeEvent(this));

        phases().next();
    }

    @Override
    public void finish(LGEnding ending) {
        getState().mustNotBe(UNINITIALIZED, FINISHED, DELETING, DELETED);

        gameData.setEnding(ending);

        changeStateTo(FINISHED, o -> new LGGameFinishedEvent(o, ending));

        phases().next();
    }

    @Override
    public void delete() {
        getState().mustNotBe(DELETING, DELETED);

        changeStateTo(DELETING, LGGameDeletingEvent::new);

        gameData.getPlayers().forEach(this::leave);
        terminableRegistry.closeAndReportException();

        changeStateTo(DELETED, LGGameDeletedEvent::new);
    }

    @Override
    public void nextTimeOfDay() {
        getState().mustBe(STARTED);

        MutableLGGameTurn turn = gameData.getTurn();
        if (turn.getTime() == LGGameTurnTime.DAY) {
            turn.setTurnNumber(turn.getTurnNumber() + 1);
            turn.setTime(LGGameTurnTime.NIGHT);
        } else {
            turn.setTime(LGGameTurnTime.DAY);
        }

        Events.call(new LGTurnChangeEvent(this));
    }

    @Override
    public <T> T resolve(Class<T> clazz) {
        try (OrchestratorScope.Block ignored = scope.use(this)) {
            return injector.getInstance(clazz);
        }
    }

    @Override
    public <T> T resolve(Provider<T> provider) {
        try (OrchestratorScope.Block ignored = scope.use(this)) {
            return provider.get();
        }
    }

    private boolean deleteIfEmpty() {
        if (isEmpty() && getState().isEnabled()) {
            delete();
            return true;
        }
        return false;
    }

    private void updateLobbyStuff() {
        this.getState().mustBe(UNINITIALIZED, WAITING_FOR_PLAYERS, READY_TO_START);

        if (getState() != UNINITIALIZED) {
            validateComposition();
        }

        if (isFull() && isCompositionValid()) {
            changeStateTo(READY_TO_START, LGGameReadyToStartEvent::new);
        } else {
            changeStateTo(WAITING_FOR_PLAYERS, LGGameWaitingForPlayersEvent::new);
        }
    }

    private void checkPlayer(Player player) throws PlayerJoinException {
        // The LGGameManager approach works well for now
        // but it will cause issues with BungeeCord support.

        if (!player.isOnline()) {
            throw new PlayerOfflineException(player);
        }

        String permission = "loupsgarous.game.join";
        if (!player.hasPermission(permission)) {
            throw new PermissionMissingException(permission, player);
        }

        if (!allowsJoin()) {
            throw InaccessibleLobbyException.lobbyLocked();
        } else if (getSlotsTaken() == getTotalSlotCount()) {
            throw InaccessibleLobbyException.lobbyFull();
        }

        if (gameManager.getPlayerInGame(player).isPresent()) {
            throw new PlayerAlreadyInGameException(player);
        }
    }

    @Override
    public LGPlayer join(Player player) throws PlayerJoinException {
        checkPlayer(player);

        OrchestratedLGPlayer lgPlayer = new OrchestratedLGPlayer(player.getUniqueId(), this);
        gameData.addPlayer(lgPlayer);

        executeAfterInitialization(() -> {
            lobbyTeleporter.teleportPlayerIn(player);
            updateLobbyStuff();
            Events.call(new LGPlayerJoinEvent(this, player, lgPlayer));

            chat().sendToEveryone(player(player.getName()) + lobbyMessage(" a rejoint la partie ! ") +
                                  slots(getSlotsDisplay()));
        });

        return lgPlayer;
    }

    @Override
    public boolean leave(UUID playerUUID) {
        OrchestratedLGPlayer player = gameData.getPlayer(playerUUID)
                .filter(LGPlayer::isPresent)
                .orElse(null);
        if (player == null) return false;

        player.goAway();
        if (allowsJoin()) {
            gameData.removePlayer(playerUUID);
        }

        player.minecraftNoContext(lobbyTeleporter::teleportPlayerOut);

        if (isGameRunning() && player.isAlive()) {
            player.die(PlayerQuitKillCause.INSTANCE);
        }
        if (allowsJoin()) {
            chat().sendToEveryone(player(player.getName()) + lobbyMessage(" a quitté la partie ! ") +
                                  slots(getSlotsDisplay()));
        }

        Events.call(new LGPlayerQuitEvent(this, playerUUID, player));

        // Are they all gone?
        if (!deleteIfEmpty() && allowsJoin()) {
            updateLobbyStuff();
        }

        return true;
    }

    @Override
    public World getWorld() {
        return lobbyTeleporter.getWorld();
    }

    @Override
    public boolean allowsJoin() {
        return this.getState() == LGGameState.UNINITIALIZED ||
               this.getState() == LGGameState.WAITING_FOR_PLAYERS ||
               this.getState() == LGGameState.READY_TO_START;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public GameBundle getBundle() {
        return bundle.get();
    }

    @Override
    public Observable<GameBundle> observeBundle() {
        return bundle.observe();
    }

    private GameBundle createBundle(ModBundle modBundle) {
        return new GameBundle(modBundle.createExtensions(), this::resolve);
    }

    private void updateBundle(ModBundle modBundle) {
        long startTime = System.nanoTime();

        GameBundle oldBundle = bundle.get();
        GameBundle newBundle = createBundle(modBundle);

        bundle.set(newBundle);
        if (oldBundle != null && allowsJoin()) {
            removeBundleRemovedCards(oldBundle, newBundle);
        }

        long elapsed = System.nanoTime() - startTime;
        logger.info("GameBundle update took " + TimeUnit.NANOSECONDS.toMicros(elapsed) + "µs");
    }

    @Override
    public ImmutableComposition getComposition() {
        return composition;
    }

    @Override
    public void setComposition(Composition composition) {
        Preconditions.checkArgument(allowsJoin(),
                "Impossible to change the composition while the game is locked.");

        HashMultiset<LGCard> cards = HashMultiset.create(composition.getContents());

        // Add some cards if there are not enough cards for the players we have.
        while (cards.size() < getSlotsTaken()) {
            // TODO: What happens if VillageoisCard is not in the bundle? Hmmm?
            cards.add(VillageoisCard.INSTANCE);
        }

        this.composition = new ImmutableComposition(cards);

        executeAfterInitialization(() -> {
            updateLobbyStuff();
            Events.call(new LGLobbyCompositionUpdateEvent(this));
        });
    }

    @Override
    public @Nullable CompositionValidator.Problem.Type getWorstCompositionProblemType() {
        return worseCompositionProblemType;
    }

    @Override
    public boolean isCompositionValid() {
        return getWorstCompositionProblemType() != CompositionValidator.Problem.Type.IMPOSSIBLE;
    }

    private void removeBundleRemovedCards(GameBundle oldBundle, GameBundle newBundle) {
        Sets.SetView<LGCard> removedCards =
                Sets.difference(oldBundle.contents(CARDS), newBundle.contents(CARDS));

        if (removedCards.isEmpty()) {
            return;
        }

        ImmutableComposition newComposition = composition.with(cards -> {
            for (LGCard removedCard : removedCards) {
                cards.remove(removedCard, Integer.MAX_VALUE);
            }
        });

        setComposition(newComposition);
    }

    private void validateComposition() {
        if (!allowsJoin()) {
            return;
        }

        worseCompositionProblemType =
                getBundle().handler(COMPOSITION_VALIDATORS).validate(composition).stream()
                        .map(CompositionValidator.Problem::getType)
                        .max(Comparator.naturalOrder())
                        .orElse(null);
    }

    @Override
    public <T extends OrchestratorComponent> T component(MetadataKey<T> key) {
        checkDelayedDependencies();

        return delayedDependencies.componentMap.get(key).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Logger logger() {
        return logger;
    }

    @Override
    public String getId() {
        return gameData.getId();
    }

    @Override
    public LGGameState getState() {
        return gameData.getState();
    }

    @Override
    public ImmutableSet<LGPlayer> getPlayers() {
        return gameData.getPlayers();
    }

    @Override
    public MutableLGGameTurn getTurn() {
        return gameData.getTurn();
    }

    @Override
    @Nullable
    public LGEnding getEnding() {
        return gameData.getEnding();
    }

    @Override
    @Nullable
    public OrchestratedLGPlayer getOwner() {
        return gameData.getOwner();
    }

    @Override
    public void setOwner(LGPlayer owner) {
        LGPlayer newOwner = gameData.ensurePresent(owner);

        if (owner == gameData.getOwner()) return;

        gameData.setOwner(newOwner);

        Events.call(new LGLobbyOwnerChangeEvent(this, owner));
    }

    @Override
    public ModBundle getModBundle() {
        return modBundle.get();
    }

    @Override
    public void setModBundle(ModBundle modBundle) {
        this.modBundle.set(modBundle);
    }

    @Override
    public Observable<ModBundle> observeModBundle() {
        return modBundle.observe();
    }

    @Override
    public MetadataMap getMetadata() {
        return gameData.getMetadata();
    }

    @Override
    public Optional<? extends LGPlayer> getPlayer(UUID playerUUID) {
        return gameData.getPlayer(playerUUID);
    }

    @Override
    public LGPlayer getPlayerOrThrow(UUID playerUUID) {
        return gameData.getPlayerOrThrow(playerUUID);
    }

    @Override
    public LGPlayer ensurePresent(LGPlayer player) {
        return gameData.ensurePresent(player);
    }

    private void registerEventListeners() {
        Events.merge(PlayerEvent.class, PlayerQuitEvent.class, PlayerKickEvent.class)
                .handler(e -> leave(e.getPlayer()))
                .bindWith(this);

        Events.subscribe(PlayerChangedWorldEvent.class)
                .filter(e -> e.getFrom() == getWorld())
                .handler(e -> leave(e.getPlayer()))
                .bindWith(this);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", gameData.getId())
                .add("getState", this.getState())
                .toString();
    }

    @Nonnull
    @Override
    public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    private void changeStateTo(LGGameState state,
                               Function<? super LGGameOrchestrator, ? extends LGEvent> eventFunction) {
        if (this.getState() == state) return;

        LGGameState oldState = this.getState();
        gameData.setState(state);

        logger.fine("State changed: " + oldState + " -> " + state);

        Events.call(eventFunction.apply(this));
    }

    private void checkDelayedDependencies() {
        Preconditions.checkState(delayedDependencies != null,
                "This game is not initialized yet.");
    }

    private void executeAfterInitialization(Runnable action) {
        if (getState() == UNINITIALIZED) {
            postInitializationActions.add(action);
        } else {
            action.run();
        }
    }

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

    private class OrchestratorLogger extends Logger {
        private final String prefix;

        public OrchestratorLogger(String id) {
            super(MinecraftLGGameOrchestrator.this.getClass().getCanonicalName(), null);
            prefix = "[LoupsGarous] (Game " + id + ") ";
            setParent(getPlugin().getLogger());
            setLevel(Level.ALL);
        }

        @Override
        public void log(LogRecord record) {
            record.setMessage(prefix + record.getMessage());
            super.log(record);
        }
    }
}
