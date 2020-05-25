package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.cards.LGCardOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.chat.AnonymizedChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.events.*;
import com.github.jeuxjeux20.loupsgarous.game.stages.AsyncLGGameStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.GameEndStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.GameStartStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGGameStage;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.destination.DestinationFactory;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.github.jeuxjeux20.loupsgarous.game.MinecraftLGGameOrchestrator.OrchestratorState.*;

class MinecraftLGGameOrchestrator implements LGGameOrchestrator {
    private final LGCardOrchestrator cardOrchestrator;
    private final MultiverseCore multiverse;
    private final LoupsGarous plugin;
    private final Logger logger;
    private final LinkedList<AsyncLGGameStage.Factory<?>> stageFactories;
    private final ArrayList<LGKill> pendingKills = new ArrayList<>();
    private final HashMap<AnonymizedChatChannel, List<String>> anonymizedNames = new HashMap<>();
    private final CommandSender initiator;
    private final MultiverseWorld world;
    private final MinecraftLGGameLobby lobby;
    private final LGActionBarManager actionBarManager;
    private final UUID id;
    private final MVDestination worldDestination;
    private final Task actionBarUpdateTask;
    private final MutableLGGame game;
    private LGGameState state = LGGameState.WAITING_FOR_PLAYERS;
    private @Nullable ListIterator<AsyncLGGameStage.Factory<?>> stageIterator = null;
    private @Nullable AsyncLGGameStage currentStage = null;
    private @Nullable CompletableFuture<Void> currentStageFuture = null;

    @Inject
    public MinecraftLGGameOrchestrator(@Assisted LGGameLobbyInfo lobbyInfo,
                                       Set<AsyncLGGameStage.Factory<?>> stageFactories,
                                       MultiverseCore multiverse,
                                       LoupsGarous plugin,
                                       LGActionBarManager actionBarManager,
                                       LGCardOrchestrator.Factory cardOrchestratorFactory) {
        this.id = lobbyInfo.getId();
        this.initiator = lobbyInfo.getInitiator();
        this.world = lobbyInfo.getWorld();
        this.stageFactories = new LinkedList<>(stageFactories);
        this.multiverse = multiverse;
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.actionBarManager = actionBarManager;

        this.game = new MutableLGGame(lobbyInfo.getPlayers());
        this.worldDestination = multiverse.getDestFactory().getDestination(getWorld().getName());

        this.lobby = new MinecraftLGGameLobby(lobbyInfo, this);

        this.cardOrchestrator = cardOrchestratorFactory.create(this);

        registerLobbyEvents();

        // Wait 1 second before running it for the first time, because teleporting isn't instant ;).
        this.actionBarUpdateTask = Schedulers.sync().runRepeating(this::updateActionBars, 20, 20);
    }

    private void registerLobbyEvents() {
        Events.merge(LGEvent.class, LGLobbyPlayerJoinEvent.class, LGLobbyPlayerQuitEvent.class, LGLobbyCompositionChangeEvent.class)
                .filter(this::isMyEvent)
                .expireIf(e -> lobby.isLocked())
                .handler(e -> updateLobbyState());

        Events.subscribe(LGLobbyPlayerQuitEvent.class)
                .filter(this::isMyEvent)
                .expireIf(e -> lobby.isLocked())
                .handler(this::handlePlayerQuit);

        Events.subscribe(LGLobbyPlayerJoinEvent.class)
                .filter(this::isMyEvent)
                .expireIf(e -> lobby.isLocked())
                .handler(this::handlePlayerJoin);
    }

    private boolean isMyEvent(LGEvent event) {
        return event.getOrchestrator() == this;
    }

    private void updateActionBars() {
        getGame().getPlayers().forEach(player -> actionBarManager.update(player, this));
    }

    @Override
    public MutableLGGame getGame() {
        return game;
    }

    @Override
    public LGGameLobby lobby() {
        return lobby;
    }

    @Override
    public MultiverseWorld getWorld() {
        return world;
    }

    @Override
    public LGGameState getState() {
        return state;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void killInstantly(LGKill kill) {
        ensureState(STARTED);

        killPlayer(kill);

        callEvent(new LGKillEvent(this, kill));
    }

    @Override
    public ArrayList<LGKill> getPendingKills() {
        ensureState(STARTED);

        return pendingKills;
    }

    @Override
    public void revealAllPendingKills() {
        ensureState(STARTED);

        ImmutableList<LGKill> kills = ImmutableList.copyOf(pendingKills);
        pendingKills.clear();

        for (LGKill kill : kills) {
            killPlayer(kill);
        }

        callEvent(new LGKillEvent(this, kills));
    }

    private void killPlayer(LGKill kill) {
        MutableLGPlayer whoDied = (MutableLGPlayer) kill.getWhoDied();

        Preconditions.checkArgument(whoDied.isAlive(), "Cannot kill player " + whoDied.getName() + " because they are dead.");

        whoDied.setDead(true);
    }

    @Override
    public void nextTimeOfDay() {
        ensureState(STARTED);

        MutableLGGameTurn turn = game.getTurn();
        if (turn.getTime() == LGGameTurnTime.DAY) {
            turn.setTurnNumber(turn.getTurnNumber() + 1);
            turn.setTime(LGGameTurnTime.NIGHT);
        } else {
            turn.setTime(LGGameTurnTime.DAY);
        }

        callEvent(new LGTurnChangeEvent(this));
    }


    @Override
    public void start() {
        ensureState(READY_TO_START);

        game.distributeCards(lobby.getComposition());
        changeStateTo(STARTED, LGGameStartEvent::new);

        callEvent(new LGTurnChangeEvent(this));

        callNextStage();
    }

    private void updateLobbyState() {
        ensureState(WAITING_FOR_PLAYERS, READY_TO_START);

        if (lobby.isFull()) {
            changeStateTo(READY_TO_START, LGGameReadyToStartEvent::new);
        } else {
            changeStateTo(WAITING_FOR_PLAYERS, LGGameWaitingForPlayersEvent::new);
        }
    }

    @Override
    public void finish(LGEnding ending) {
        // A game can be finished at any state except when it's already finished.
        ensureNotState(FINISHED);
        changeStateTo(FINISHED, o -> new LGGameFinishedEvent(o, ending));

        callNextStage();
    }

    @Override
    public void delete() {
        ensureNotState(DELETED);

        actionBarUpdateTask.stop();

        teleportPlayersOutAndDeleteWorld();

        changeStateTo(DELETED, LGGameDeletedEvent::new);
    }


    @Override
    public void initializeAndTeleport() {
        ensureState(WAITING_FOR_PLAYERS, READY_TO_START);

        initializeWorld();

        getAllMinecraftPlayers().forEach(this::teleportPlayerIn);

        if (stageIterator == null) {
            callNextStage();
        }
    }

    public void teleportPlayerIn(Player player) {
        if (player.getWorld() == world.getCBWorld()) return;
        multiverse.getSafeTTeleporter().teleport(initiator, player, worldDestination);
    }

    private void initializeWorld() {
        getWorld().setDifficulty(Difficulty.PEACEFUL);
        getWorld().setGameMode(GameMode.ADVENTURE);
        getWorld().setAllowAnimalSpawn(false);
        getWorld().setAllowMonsterSpawn(false);
        getWorld().setTime("day");
        getWorld().setPVPMode(false);
        getWorld().setRespawnToWorld(getWorld().getName());
        getWorld().getCBWorld().setGameRule(GameRule.FALL_DAMAGE, false);
        getWorld().getCBWorld().setGameRule(GameRule.KEEP_INVENTORY, true);
        getWorld().getCBWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
    }

    private void teleportPlayersOutAndDeleteWorld() {
        for (LGPlayer player : getGame().getPlayers()) {
            player.getMinecraftPlayer().ifPresent(minecraftPlayer -> teleportPlayerOut(minecraftPlayer, player));
        }
        multiverse.getMVWorldManager().deleteWorld(getWorld().getName());
    }

    private void teleportPlayerOut(Player minecraftPlayer, @Nullable LGPlayer player) {
        LGGamePlayerQuitTeleportEvent event
                = new LGGamePlayerQuitTeleportEvent(this, player, minecraftPlayer);
        callEvent(event);
        if (event.isCancelled()) return;

        MultiverseWorld spawnWorld = multiverse.getMVWorldManager().getSpawnWorld();

        DestinationFactory destFactory = multiverse.getDestFactory();
        MVDestination destination = destFactory.getDestination(spawnWorld.getName());

        multiverse.getSafeTTeleporter().teleport(initiator, minecraftPlayer, destination);
    }

    private void handlePlayerJoin(LGLobbyPlayerJoinEvent event) {
        teleportPlayerIn(event.getPlayer());

        sendToEveryone(event.getPlayer().getName() + " a rejoint la partie ! " + lobby.getSlotsDisplay());
    }

    private void handlePlayerQuit(LGLobbyPlayerQuitEvent e) {
        sendToEveryone(e.getPlayer().getName() + " a quitté la partie ! " + lobby.getSlotsDisplay());

        if (getGame().getPlayers().isEmpty()) {
            delete();
        }

        teleportPlayerOut(e.getPlayer(), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addStage(AsyncLGGameStage.Factory<?> stage) {
        if (stageIterator == null) {
            stageFactories.add(stage);
        } else {
            stageIterator.add(stage);
            stageIterator.previous();
        }
    }

    @Override
    public @NotNull LGGameStage getCurrentStage() {
        return currentStage == null ? new LGGameStage.Null() : currentStage;
    }

    @Override
    public final LoupsGarous getPlugin() {
        return plugin;
    }

    public void callEvent(LGEvent event) {
        plugin.getServer().getPluginManager().callEvent(event);
    }

    @Override
    public HashMap<AnonymizedChatChannel, List<String>> getAnonymizedNames() {
        return anonymizedNames;
    }

    @Override
    public LGCardOrchestrator cards() {
        return cardOrchestrator;
    }

    private void callNextStage() {
        if (currentStageFuture != null && !currentStageFuture.isDone()) currentStageFuture.cancel(true);

        if (callSpecialStages()) return;

        if (stageFactories.size() == 0)
            throw new IllegalStateException("No stages have been found.");

        if (stageIterator == null || !stageIterator.hasNext())
            stageIterator = stageFactories.listIterator(); // Reset the iterator

        AsyncLGGameStage.Factory<?> factory = stageIterator.next();
        AsyncLGGameStage stage = factory.create(this);

        if (stage.isTemporary())
            stageIterator.remove();

        if (stage.shouldRun()) {
            logger.info("Running stage: " + stage.getClass().getName());
            updateAndRunCurrentStage(stage).exceptionally(ex -> {
                // We cancelled it, no need to log.
                if (!(ex instanceof CancellationException)) {
                    logger.log(Level.SEVERE, "Unhandled exception while running stage: " + stage, ex);
                }
                throw (RuntimeException) ex; // We cannot throw checked exceptions.
            }).thenRun(this::callNextStage);
        } else {
            callNextStage();
        }
    }

    private boolean callSpecialStages() {
        if (state == LGGameState.READY_TO_START || state == LGGameState.WAITING_FOR_PLAYERS) {
            if (!(currentStage instanceof GameStartStage)) {
                GameStartStage stage = new GameStartStage(this);
                updateAndRunCurrentStage(stage).thenRun(this::start).exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Unhandled exception while the game was running: ", ex);
                    return null;
                });
            }
            return true;
        }
        if (state == LGGameState.FINISHED) {
            if (!(currentStage instanceof GameEndStage)) {
                GameEndStage stage = new GameEndStage(this);
                updateAndRunCurrentStage(stage).thenRun(this::delete).exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Unhandled exception while deleting the game: ", ex);
                    return null;
                });
            }
            return true;
        }
        return false;
    }

    private CompletionStage<Void> updateAndRunCurrentStage(AsyncLGGameStage stage) {
        currentStage = stage;
        callEvent(new LGStageChangeEvent(this, stage));
        CompletableFuture<Void> future = stage.run();
        currentStageFuture = future;
        logger.info("current future is now: " + future);
        return future;
    }

    // State stuff

    /**
     * Changes the current state to the specified {@code state}, and calls the event created using the given
     * function.
     *
     * @param state         the state to change to
     * @param eventFunction the function that creates the event to call
     * @param <E>           the type of the event
     * @throws IllegalStateException when the state's game type is not the same as the current one
     */
    private <E extends LGEvent> void changeStateTo(OrchestratorState<E> state,
                                                   Function<? super LGGameOrchestrator, E> eventFunction) {
        this.state = state.value;

        callEvent(eventFunction.apply(this));
    }

    private void ensureNotState(OrchestratorState<?> state) {
        Preconditions.checkState(this.state != state.value,
                "The game state must NOT be: " + this.state.toString());
    }

    /**
     * Ensures that the current state is the same as the specified one, if it isn't, throws an exception.
     *
     * @param state the state to check
     * @throws IllegalStateException when the current state is not the same as the given one
     */
    private void ensureState(OrchestratorState<?> state) {
        Preconditions.checkState(this.state == state.value,
                "The game state must be: " + state.toString());
    }

    private void ensureState(OrchestratorState<?>... states) {
        for (OrchestratorState<?> state : states) {
            if (this.state == state.value) return;
        }

        throw new IllegalStateException("The game state must be in [" +
                Arrays.stream(states).map(Object::toString).collect(Collectors.joining(", ")) + "].");
    }

    /**
     * Represents the game state of the orchestrator, which wraps a {@link LGGameState} {@linkplain #value}, with some
     * the type of the event to call when changing to this state ({@code <E>}).<br>
     * <i>Example:</i> The {@link #FINISHED} state has an event type of {@link LGGameFinishedEvent}.<br>
     * <i>Note:</i> Do not use wildcards for events (such as {@code ? extends E}), since
     * events listeners only listens for concrete types and not subclasses.
     *
     * @param <E> the type of the event to call
     */
    static class OrchestratorState<E extends LGEvent> {
        public static final OrchestratorState<LGGameWaitingForPlayersEvent> WAITING_FOR_PLAYERS
                = new OrchestratorState<>(LGGameState.WAITING_FOR_PLAYERS);

        public static final OrchestratorState<LGGameReadyToStartEvent> READY_TO_START
                = new OrchestratorState<>(LGGameState.READY_TO_START);

        public static final OrchestratorState<LGGameStartEvent> STARTED
                = new OrchestratorState<>(LGGameState.STARTED);

        public static final OrchestratorState<LGGameFinishedEvent> FINISHED
                = new OrchestratorState<>(LGGameState.FINISHED);

        public static final OrchestratorState<LGGameDeletedEvent> DELETED
                = new OrchestratorState<>(LGGameState.DELETED);

        public final LGGameState value;

        private OrchestratorState(LGGameState value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }
}
