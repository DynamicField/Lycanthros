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
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.jeuxjeux20.loupsgarous.game.MinecraftLGGameOrchestrator.OrchestratorState.*;

class MinecraftLGGameOrchestrator implements LGGameOrchestrator {
    private final LGCardOrchestrator cardOrchestrator;
    private final LGGameLobbyInfo lobbyInfo;
    private final MultiverseCore multiverse;
    private final LoupsGarous plugin;
    private final Logger logger;
    private final LinkedList<AsyncLGGameStage.Factory<?>> stageFactories;
    private final ArrayList<LGKill> pendingKills = new ArrayList<>();
    private final HashMap<AnonymizedChatChannel, List<String>> anonymizedNames = new HashMap<>();
    private LGGame game;
    private LGGameState state = LGGameState.WAITING_FOR_PLAYERS;
    private @Nullable ListIterator<AsyncLGGameStage.Factory<?>> stageIterator = null;
    private @Nullable AsyncLGGameStage currentStage = null;
    private @Nullable CompletableFuture<Void> currentStageFuture = null;

    @Inject
    public MinecraftLGGameOrchestrator(@Assisted LGGameLobbyInfo gameLobbyInfo,
                                       Set<AsyncLGGameStage.Factory<?>> stageFactories,
                                       MultiverseCore multiverse,
                                       LoupsGarous plugin,
                                       LGCardOrchestrator.Factory cardOrchestratorFactory) {
        this.lobbyInfo = gameLobbyInfo;
        this.stageFactories = new LinkedList<>(stageFactories);
        this.multiverse = multiverse;
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.game = new UndeterminedLGGame(gameLobbyInfo.getPlayerUUIDs());
        this.cardOrchestrator = cardOrchestratorFactory.create(this);
    }

    @Override
    public void teleportAllPlayers() {
        ensureState(WAITING_FOR_PLAYERS);

        DestinationFactory destFactory = multiverse.getDestFactory();
        MVDestination destination = destFactory.getDestination(getWorld().getName());

        initializeWorld();

        getAllMinecraftPlayers()
                .forEach(player -> multiverse.getSafeTTeleporter().teleport(lobbyInfo.getInitiator(), player, destination));

        changeStateTo(READY_TO_START, LGGameReadyToStartEvent::new);

        callNextStage();
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

    @Override
    public void start() {
        ensureState(READY_TO_START);

        RunningLGGame game = RunningLGGame.create(lobbyInfo.getPlayers(), lobbyInfo.getComposition(), multiverse);
        changeStateTo(STARTED, game, LGGameStartEvent::new);

        callEvent(new LGTurnChangeEvent(this));

        callNextStage();
    }

    @Override
    public LGGame getGame() {
        return game;
    }

    @Override
    public LGGameLobbyInfo getLobbyInfo() {
        return lobbyInfo;
    }

    @Override
    public MultiverseWorld getWorld() {
        return lobbyInfo.getWorld();
    }

    @Override
    public boolean hasGameStarted() {
        return game != null;
    }

    @Override
    public LGGameState getState() {
        return state;
    }

    @Override
    public UUID getId() {
        return lobbyInfo.getId();
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
        ensureState(STARTED, game -> {
            MutableLGGameTurn turn = game.getTurn();
            if (turn.getTime() == LGGameTurnTime.DAY) {
                turn.setTurnNumber(turn.getTurnNumber() + 1);
                turn.setTime(LGGameTurnTime.NIGHT);
            } else {
                turn.setTime(LGGameTurnTime.DAY);
            }
            callEvent(new LGTurnChangeEvent(this));
        });
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
        ensureState(FINISHED);

        teleportPlayersAndDeleteWorld();

        changeStateTo(DELETED, LGGameDeletedEvent::new);
    }

    private void teleportPlayersAndDeleteWorld() {
        for (LGPlayer player : getGame().getPlayers()) {
            player.getMinecraftPlayer().ifPresent(minecraftPlayer -> teleportPlayer(minecraftPlayer, player));
        }
        multiverse.getMVWorldManager().deleteWorld(getWorld().getName());
    }

    private void teleportPlayer(Player minecraftPlayer, LGPlayer player) {
        LGGameDeletingPlayerTeleportEvent event
                = new LGGameDeletingPlayerTeleportEvent(this, player, minecraftPlayer);
        callEvent(event);
        if (event.isCancelled()) return;

        MultiverseWorld previousWorld = player.getPreviousWorld();
        if (previousWorld == null) previousWorld = multiverse.getMVWorldManager().getSpawnWorld();

        DestinationFactory destFactory = multiverse.getDestFactory();
        MVDestination destination = destFactory.getDestination(previousWorld.getName());

        multiverse.getSafeTTeleporter().teleport(lobbyInfo.getInitiator(), minecraftPlayer, destination);
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
        if (state == LGGameState.READY_TO_START) {
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
        callEvent(new LGStageChangedEvent(this, stage));
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
     * @throws IllegalStateException when the state's game type is not the same as the current one
     * @param state the state to change to
     * @param eventFunction the function that creates the event to call
     * @param <E> the type of the event
     */
    private <E extends LGEvent> void changeStateTo(OrchestratorState<?, E> state,
                                                   Function<? super LGGameOrchestrator, E> eventFunction) {
        if (!state.gameClass.isInstance(game)) {
            throw new IllegalStateException(
                    "Cannot have a game being an instance of " + game.getClass().getSimpleName() +
                    " with the state " + state.value + "."
            );
        }

        this.state = state.value;

        callEvent(eventFunction.apply(this));
    }

    /**
     * Changes the current state to the specified {@code state}, calls the event created using the given
     * function and changes the current game to the specified {@code newGame}.
     *
     * @param state the state to change to
     * @param newGame the new game
     * @param eventFunction the function that creates the event to call
     * @param <G> the type of the game
     * @param <E> the type of the event
     */
    private <G extends LGGame, E extends LGEvent>
    void changeStateTo(OrchestratorState<? extends G, E> state, G newGame,
                       Function<? super LGGameOrchestrator, E> eventFunction) {
        this.state = state.value;
        this.game = newGame;

        callEvent(eventFunction.apply(this));
    }

    /**
     * Ensures that the current state is the same as the specified one, if so, runs the {@code gameConsumer}
     * with the game casted to the state's game type, else throws an exception.
     *
     * @throws IllegalStateException when the current state is not the same as the given one
     * @param state the state to check
     * @param gameConsumer the function to run when
     * @param <G> the type of the game
     */
    private <G extends LGGame> void ensureState(OrchestratorState<? extends G, ?> state,
                                                Consumer<? super G> gameConsumer) {
        ensureState(state);

        G gameCasted = state.gameClass.cast(game);
        gameConsumer.accept(gameCasted);
    }

    private void ensureNotState(OrchestratorState<?, ?> state) {
        Preconditions.checkState(this.state != state.value,
                "The game state must NOT be: " + this.state.toString());
    }
    /**
     * Ensures that the current state is the same as the specified one, if it isn't, throws an exception.
     *
     * @throws IllegalStateException when the current state is not the same as the given one
     * @param state the state to check
     */
    private void ensureState(OrchestratorState<?, ?> state) {
        Preconditions.checkState(this.state == state.value,
                "The game state must be: " + this.state.toString());
    }

    /**
     * Represents the game state of the orchestrator, which wraps a {@link LGGameState} {@linkplain #value}, with some
     * additional information:
     * <ul>
     *     <li>
     *         The type of game to have when having this state ({@code <G>} and {@link #gameClass}).<br>
     *         <i>Example:</i> The {@link #STARTED} state has a game type of {@link RunningLGGame}.
     *     </li>
     *     <li>
     *         The type of the event to call when changing to this state ({@code <E>}).<br>
     *         <i>Example:</i> The {@link #FINISHED} state has an event type of {@link LGGameFinishedEvent}.<br>
     *         <i>Note:</i> Do not use wildcards for events (such as {@code ? extends E}), since
     *         events listeners only listens for concrete types and not subclasses.
     *     </li>
     * </ul>
     *
     * @param <G> the type of game to have when having this state
     * @param <E> the type of the event to call
     */
    static class OrchestratorState<G extends LGGame, E extends LGEvent> {
        public static final OrchestratorState<UndeterminedLGGame, LGGameWaitingForPlayersEvent> WAITING_FOR_PLAYERS
                = new OrchestratorState<>(LGGameState.WAITING_FOR_PLAYERS, UndeterminedLGGame.class);

        public static final OrchestratorState<UndeterminedLGGame, LGGameReadyToStartEvent> READY_TO_START
                = new OrchestratorState<>(LGGameState.READY_TO_START, UndeterminedLGGame.class);

        public static final OrchestratorState<RunningLGGame, LGGameStartEvent> STARTED
                = new OrchestratorState<>(LGGameState.STARTED, RunningLGGame.class);

        // Maybe that the game has finished while it was undetermined.
        public static final OrchestratorState<LGGame, LGGameFinishedEvent> FINISHED
                = new OrchestratorState<>(LGGameState.FINISHED, LGGame.class);

        // Same as finished.
        public static final OrchestratorState<LGGame, LGGameDeletedEvent> DELETED
                = new OrchestratorState<>(LGGameState.DELETED, LGGame.class);

        public final LGGameState value;
        public final Class<G> gameClass;

        private OrchestratorState(LGGameState value, Class<G> gameClass) {
            this.value = value;
            this.gameClass = gameClass;
        }
    }
}
