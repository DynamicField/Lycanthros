package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.LoupsGarousRoot;
import com.github.jeuxjeux20.loupsgarous.actionbar.LGActionBarManager;
import com.github.jeuxjeux20.loupsgarous.bossbar.LGBossBarManager;
import com.github.jeuxjeux20.loupsgarous.cards.CardDistributor;
import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.cards.VillageoisCard;
import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.cards.composition.ImmutableComposition;
import com.github.jeuxjeux20.loupsgarous.chat.ActualChatOrchestrator;
import com.github.jeuxjeux20.loupsgarous.chat.ChatOrchestrator;
import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.event.*;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGCompositionUpdateEvent;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGOwnerChangeEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.extensibility.GameBox;
import com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints;
import com.github.jeuxjeux20.loupsgarous.extensibility.ModRegistry;
import com.github.jeuxjeux20.loupsgarous.interaction.ActualInteractableRegistry;
import com.github.jeuxjeux20.loupsgarous.interaction.InteractableRegistry;
import com.github.jeuxjeux20.loupsgarous.inventory.LGInventoryManager;
import com.github.jeuxjeux20.loupsgarous.kill.ActualKillsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.kill.KillsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.kill.causes.PlayerQuitKillCause;
import com.github.jeuxjeux20.loupsgarous.lobby.*;
import com.github.jeuxjeux20.loupsgarous.phases.*;
import com.github.jeuxjeux20.loupsgarous.scoreboard.LGScoreboardManager;
import com.github.jeuxjeux20.loupsgarous.storage.MapStorage;
import com.github.jeuxjeux20.loupsgarous.storage.Storage;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.reactivex.rxjava3.disposables.Disposable;
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

import static com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff.*;
import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.*;

class MinecraftLGGameOrchestrator implements LGGameOrchestrator {
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();

    private final String id;
    private LGGameState state = LOBBY;
    private final Map<UUID, LGPlayer> players = new HashMap<>();
    private LGPlayer owner;
    private ImmutableComposition composition;
    private final MutableLGGameTurn turn = new MutableLGGameTurn();
    private @Nullable LGEnding ending;
    private boolean endingWhenEmpty = false;

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
    private final GameBox gameBox;
    private final Storage storage = new MapStorage();

    private final Disposable cardRemovalSubscription;

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

            this.gameBox = bind(new GameBox(this, modRegistry));
            this.cardRemovalSubscription = gameBox.updates().subscribe(this::removeBoxRemovedCards);
            bind(cardRemovalSubscription::dispose);

            this.componentManager = new OrchestratorComponentManager(this);
            bind(componentManager::close);

            registerEventListeners();

            gameBox.addMods(modRegistry.getMods());

            new LobbyPhaseCycle(this).start();
        } catch (Throwable e) {
            delete();
            throw e;
        }
    }

    private void start() {
        state.mustBe(LOBBY);

        cardRemovalSubscription.dispose();
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
    public GameBox getGameBox() {
        return gameBox;
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
            Events.call(new LGCompositionUpdateEvent(this));
        }
    }

    private void removeBoxRemovedCards(GameBox.Change change) {
        ImmutableSet<LGCard> removedCards =
                change.getContentsDiff(LGExtensionPoints.CARDS).getRemoved();

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
