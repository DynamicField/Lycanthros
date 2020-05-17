package com.github.df.loupsgarous.game;

import com.github.df.loupsgarous.LoupsGarous;
import com.github.df.loupsgarous.LoupsGarousRoot;
import com.github.df.loupsgarous.actionbar.LGActionBarManager;
import com.github.df.loupsgarous.bossbar.LGBossBarManager;
import com.github.df.loupsgarous.cards.CardDistributor;
import com.github.df.loupsgarous.cards.LGCard;
import com.github.df.loupsgarous.cards.VillageoisCard;
import com.github.df.loupsgarous.cards.composition.Composition;
import com.github.df.loupsgarous.cards.composition.ImmutableComposition;
import com.github.df.loupsgarous.chat.ActualChatOrchestrator;
import com.github.df.loupsgarous.chat.ChatOrchestrator;
import com.github.df.loupsgarous.endings.LGEnding;
import com.github.df.loupsgarous.event.*;
import com.github.df.loupsgarous.lobby.*;
import com.github.df.loupsgarous.phases.*;
import com.github.df.loupsgarous.event.*;
import com.github.df.loupsgarous.event.lobby.LGCompositionChangeEvent;
import com.github.df.loupsgarous.event.lobby.LGOwnerChangeEvent;
import com.github.df.loupsgarous.event.player.LGPlayerJoinEvent;
import com.github.df.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.df.loupsgarous.event.registry.RegistryChangeEvent;
import com.github.df.loupsgarous.extensibility.GameModsContainer;
import com.github.df.loupsgarous.extensibility.ModEntry;
import com.github.df.loupsgarous.extensibility.ModRegistry;
import com.github.df.loupsgarous.extensibility.registry.GameRegistries;
import com.github.df.loupsgarous.extensibility.registry.GameRegistryKey;
import com.github.df.loupsgarous.extensibility.registry.Registry;
import com.github.df.loupsgarous.interaction.ActualInteractableRegistry;
import com.github.df.loupsgarous.interaction.InteractableRegistry;
import com.github.df.loupsgarous.inventory.LGInventoryManager;
import com.github.df.loupsgarous.kill.ActualKillsOrchestrator;
import com.github.df.loupsgarous.kill.KillsOrchestrator;
import com.github.df.loupsgarous.kill.causes.PlayerQuitKillCause;
import com.github.df.loupsgarous.lobby.*;
import com.github.df.loupsgarous.phases.*;
import com.github.df.loupsgarous.scoreboard.LGScoreboardManager;
import com.github.df.loupsgarous.storage.MapStorage;
import com.github.df.loupsgarous.storage.Storage;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static com.github.df.loupsgarous.chat.LGChatStuff.*;
import static com.github.df.loupsgarous.game.LGGameState.*;

class MinecraftLGGameOrchestrator implements LGGameOrchestrator {
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();

    private final String id;
    private final MutableLGGameTurn turn = new MutableLGGameTurn();
    private final Map<UUID, LGPlayer> players = new HashMap<>();
    private final Map<GameRegistryKey<?>, Registry<?>> gameRegistries = new HashMap<>();
    private LGGameState state = LOBBY;
    private LGPlayer owner;
    private ImmutableComposition composition;
    private @Nullable LGEnding ending;
    private boolean endingWhenEmpty;

    private final LoupsGarous plugin;
    private final OrchestratorLogger logger;
    private final LGGameManager gameManager;
    private final StateTransitionHandler stateTransitionHandler;
    private final LobbyTeleporter lobbyTeleporter;
    private final OrchestratorComponentManager componentManager;
    private final PhasesOrchestrator phases;
    private final ChatOrchestrator chat;
    private final KillsOrchestrator kills;
    private final ActualInteractableRegistry interactables;
    private final GameModsContainer modsContainer;
    private final Storage storage = new MapStorage();

    @Inject
    MinecraftLGGameOrchestrator(@Assisted LGGameBootstrapData data,
            LoupsGarous plugin,
            LobbyTeleporter.Factory lobbyTeleporterFactory,
            LGGameManager gameManager,
            ModRegistry modRegistry) throws GameCreationException {
        try {
            this.id = data.getId();
            this.lobbyTeleporter = bind(lobbyTeleporterFactory.create());
            doSetComposition(data.getComposition(), false);

            this.gameManager = gameManager;
            this.plugin = plugin;
            this.stateTransitionHandler = new StateTransitionHandler(this);
            this.logger = new OrchestratorLogger();

            this.phases = bind(new ActualPhasesOrchestrator(this));
            this.chat = bind(new ActualChatOrchestrator(this));
            this.kills = bind(new ActualKillsOrchestrator(this));
            this.interactables = bind(new ActualInteractableRegistry(this));

            this.modsContainer = bind(new GameModsContainer(this, modRegistry));

            this.componentManager = new OrchestratorComponentManager(this);
            bind(componentManager::close);

            registerEventListeners();

            for (ModEntry entry : modRegistry.getEntries()) {
                modsContainer.addMods(entry.getModFactory().create(this));
            }

            Events.subscribe(RegistryChangeEvent.class)
                    .filter(e -> e.getRegistry() == getGameRegistry(GameRegistries.CARDS))
                    .handler(e -> removeBoxRemovedCards())
                    .bindWith(this);

            new LobbyPhaseProgram(this).start();
        } catch (Throwable e) {
            delete();
            throw e;
        }
    }

    private void start() {
        state.mustBe(LOBBY);

        new CardDistributor().distribute(composition, players.values());

        changeStateTo(STARTED, LGGameStartEvent::new);
        Events.call(new LGTurnChangeEvent(this));

        new GamePhaseCycle(this).start();
    }

    private void finish(LGEnding ending) {
        state.mustNotBe(FINISHED, DELETING, DELETED);

        this.ending = ending;

        changeStateTo(FINISHED, o -> new LGGameFinishedEvent(o, ending));

        new GameEndPhaseProgram(this).start();
    }

    private void delete() {
        state.mustNotBe(DELETING, DELETED);

        changeStateTo(DELETING, LGGameDeletingEvent::new);

        players.values().forEach(this::leave);
        terminableRegistry.closeAndReportException();

        changeStateTo(DELETED, LGGameDeletedEvent::new);
    }

    @Override
    public void nextTimeOfDay() {
        state.mustBe(STARTED);

        if (turn.getTime() == LGGameTurnTime.DAY) {
            turn.setTurnNumber(turn.getTurnNumber() + 1);
            turn.setTime(LGGameTurnTime.NIGHT);
        } else {
            turn.setTime(LGGameTurnTime.DAY);
        }

        Events.call(new LGTurnChangeEvent(this));
    }

    void dispatchStateTransition(StateTransition transition) {
        if (transition instanceof StartGameTransition) {
            start();
        } else if (transition instanceof FinishGameTransition) {
            finish(((FinishGameTransition) transition).getEnding());
        } else if (transition instanceof DeleteGameTransition) {
            delete();
        } else {
            throw new UnsupportedOperationException(
                    "Unknown state transition " + transition);
        }
    }

    private void deleteIfEmpty() {
        if (endingWhenEmpty && isEmpty() && state.isEnabled()) {
            stateTransitionHandler.requestExecutionOverride(new DeleteGameTransition());
        }
    }

    @Override
    public boolean isEndingWhenEmpty() {
        return endingWhenEmpty;
    }

    @Override
    public void setEndingWhenEmpty(boolean endingWhenEmpty) {
        this.endingWhenEmpty = endingWhenEmpty;
        if (endingWhenEmpty) {
            deleteIfEmpty();
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
        } else if (getPlayersCount() == getMaxPlayers()) {
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
        players.put(lgPlayer.getPlayerUUID(), lgPlayer);

        if (owner == null) {
            owner = lgPlayer;
        }

        lobbyTeleporter.teleportPlayerIn(player);
        Events.call(new LGPlayerJoinEvent(this, player, lgPlayer));

        chat().sendToEveryone(player(player.getName()) + lobbyMessage(" a rejoint la partie ! ") +
                              slots(getSlotsDisplay()));

        return lgPlayer;
    }

    @Override
    public boolean leave(UUID playerUUID) {
        LGPlayer player = players.get(playerUUID);
        if (player == null || player.isAway()) { return false; }

        ((OrchestratedLGPlayer) player).goAway();
        if (allowsJoin()) {
            players.remove(playerUUID);
        }

        player.minecraftNoContext(lobbyTeleporter::teleportPlayerOut);

        if (isGameRunning() && player.isAlive()) {
            player.die(PlayerQuitKillCause.INSTANCE);
        }
        if (allowsJoin()) {
            chat().sendToEveryone(
                    player(player.getName()) + lobbyMessage(" a quitté la partie ! ") +
                    slots(getSlotsDisplay()));
        }

        Events.call(new LGPlayerQuitEvent(this, playerUUID, player));

        // Are they all gone?
        deleteIfEmpty();

        return true;
    }

    @Override
    public World getWorld() {
        return lobbyTeleporter.getWorld();
    }

    @Override
    public boolean allowsJoin() {
        return state == LOBBY;
    }

    @Override
    public LoupsGarousRoot getLoupsGarous() {
        return plugin;
    }

    @Override
    public GameModsContainer getModsContainer() {
        return modsContainer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Registry<T> getGameRegistry(GameRegistryKey<T> key) {
        return (Registry<T>) gameRegistries.computeIfAbsent(key, k -> k.createRegistry(this));
    }

    @Override
    public ImmutableMap<GameRegistryKey<?>, Registry<?>> getGameRegistries() {
        return ImmutableMap.copyOf(gameRegistries);
    }

    @Override
    public ImmutableComposition getComposition() {
        return composition;
    }

    @Override
    public void setComposition(Composition composition) {
        doSetComposition(composition, true);
    }

    private void doSetComposition(Composition composition, boolean raiseEvent) {
        Preconditions.checkArgument(allowsJoin(),
                "Impossible to change the composition while the game is not in is lobby phase.");

        HashMultiset<LGCard> cards = HashMultiset.create(composition.getContents());

        // Add some cards if there are not enough cards for the players we have.
        while (cards.size() < getPlayersCount()) {
            // TODO: What happens if VillageoisCard is not in the bundle? Hmmm?
            cards.add(VillageoisCard.INSTANCE);
        }

        this.composition = new ImmutableComposition(cards);
        if (raiseEvent) {
            Events.call(new LGCompositionChangeEvent(this));
        }
    }

    private void removeBoxRemovedCards() {
        ImmutableComposition newComposition = composition.with(cards -> {
            for (LGCard card : composition.getContents()) {
                if (!getGameRegistry(GameRegistries.CARDS).containsValue(card)) {
                    cards.remove(card, Integer.MAX_VALUE);
                }
            }
        });

        setComposition(newComposition);
    }

    @Override
    public Logger logger() {
        return logger;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public LGGameState getState() {
        return state;
    }

    @Override
    public ImmutableSet<LGPlayer> getPlayers() {
        return ImmutableSet.copyOf(players.values());
    }

    @Override
    public LGGameTurn getTurn() {
        return turn;
    }

    @Override
    @Nullable
    public LGEnding getEnding() {
        return ending;
    }

    @Override
    @Nullable
    public LGPlayer getOwner() {
        return owner;
    }

    @Override
    public void setOwner(LGPlayer owner) {
        if (this.owner == owner) { return; }
        this.owner = owner;

        Events.call(new LGOwnerChangeEvent(this, owner));
    }

    @Override
    public Storage getStorage() {
        return storage;
    }

    @Override
    public Optional<LGPlayer> getPlayer(UUID playerUUID) {
        return Optional.ofNullable(players.get(playerUUID));
    }

    @Override
    public LGPlayer getPlayerOrThrow(UUID playerUUID) {
        LGPlayer player = players.get(playerUUID);
        if (player == null) {
            throw new PlayerAbsentException(
                    "The given player UUID " + playerUUID +
                    " is not present in game " + this);
        }
        return player;
    }

    @Override
    public LGPlayer ensurePresent(LGPlayer player) {
        if (!players.containsValue(player)) {
            throw new PlayerAbsentException(
                    "The given player " + player + " is not present in game " + this);
        }
        return player;
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
                .add("id", id)
                .add("state", state)
                .toString();
    }

    @Nonnull
    @Override
    public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    private void changeStateTo(LGGameState state,
            @Nullable Function<? super LGGameOrchestrator, ? extends LGEvent> eventFunction) {
        if (this.getState() == state) { return; }

        LGGameState oldState = this.getState();
        this.state = state;

        logger.fine("State changed: " + oldState + " -> " + state);

        if (eventFunction != null) {
            Events.call(eventFunction.apply(this));
        }
    }

    @Override
    public StateTransitionHandler stateTransitions() {
        return stateTransitionHandler;
    }

    @Override
    public ChatOrchestrator chat() {
        return chat;
    }

    @Override
    public PhasesOrchestrator phases() {
        return phases;
    }

    @Override
    public KillsOrchestrator kills() {
        return kills;
    }

    @Override
    public InteractableRegistry interactables() {
        return interactables;
    }

    @Override
    public LGActionBarManager actionBar() {
        return componentManager.get(LGActionBarManager.class)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public LGBossBarManager bossBar() {
        return componentManager.get(LGBossBarManager.class)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OrchestratorComponentManager components() {
        return componentManager;
    }

    @Override
    public LGScoreboardManager scoreboard() {
        return componentManager.get(LGScoreboardManager.class)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public LGInventoryManager inventory() {
        return componentManager.get(LGInventoryManager.class)
                .orElseThrow(NoSuchElementException::new);
    }

    private class OrchestratorLogger extends Logger {
        public OrchestratorLogger() {
            super(getLoupsGarous().getLogger().getName(), null);
            setParent(getLoupsGarous().getLogger());
            setLevel(Level.ALL);
        }

        @Override
        public void log(LogRecord record) {
            record.setMessage(getPrefix() + record.getMessage());
            super.log(record);
        }

        private String getPrefix() {
            return (id == null ? "(Pre-initialization)" : "(Game " + id + ")") +
                   " ";
        }
    }
}
