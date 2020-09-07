package com.github.jeuxjeux20.loupsgarous.scoreboard;

import com.github.jeuxjeux20.loupsgarous.HasTriggers;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints;
import com.github.jeuxjeux20.loupsgarous.game.AbstractOrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.ClassArrayUtils;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import io.reactivex.rxjava3.disposables.Disposable;
import me.lucko.helper.Events;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LGScoreboardManager extends AbstractOrchestratorComponent {
    private static final MetadataKey<Scoreboard> SCOREBOARD_KEY
            = MetadataKey.create("lg_scoreboard", Scoreboard.class);

    private final ScoreboardComponentRenderer componentRenderer;

    @Inject
    LGScoreboardManager(LGGameOrchestrator orchestrator,
                        ScoreboardComponentRenderer componentRenderer) {
        super(orchestrator);
        this.componentRenderer = componentRenderer;

        registerEvents();
    }

    private void registerEvents() {
        Class<? extends LGEvent>[] classes =
                ClassArrayUtils.merge(getScoreboardComponents().stream().map(HasTriggers::getUpdateTriggers));

        Events.merge(LGEvent.class, classes) // Safe because of getUpdateTriggers().
                .filter(orchestrator::isMyEvent)
                .handler(e -> updateAll())
                .bindWith(this);

        Events.subscribe(LGPlayerJoinEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> updatePlayer(e.getLGPlayer()))
                .bindWith(this);

        Events.subscribe(LGPlayerQuitEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> removePlayer(e.getLGPlayer()))
                .bindWith(this);

        bind(Disposable.toAutoCloseable(
            orchestrator.getGameBox().onChange().subscribe(x -> updateAll())
        ));
    }

    public void updatePlayer(LGPlayer player) {
        player.minecraft(minecraftPlayer -> {
            Scoreboard scoreboard = Metadata.provideForPlayer(minecraftPlayer).getOrPut(SCOREBOARD_KEY,
                    () -> Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());

            update(player, scoreboard);
            minecraftPlayer.setScoreboard(scoreboard);
        });
    }

    public void removePlayer(Player player) {
        Metadata.provideForPlayer(player).get(SCOREBOARD_KEY).ifPresent(scoreboard -> {
            player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard());
            Metadata.provideForPlayer(player).remove(SCOREBOARD_KEY);
        });
    }

    private void update(LGPlayer player, Scoreboard scoreboard) {
        Objective objective = recreateObjective(scoreboard);

        componentRenderer.renderObjective(objective, getScoreboardComponents(), player, orchestrator);
    }

    private void updateAll() {
        for (LGPlayer player : orchestrator.getPlayers()) {
            updatePlayer(player);
        }
    }

    @NotNull
    private Objective recreateObjective(Scoreboard scoreboard) {
        Objective objective = scoreboard.getObjective("lg");
        if (objective != null) {
            objective.unregister();
        }
        objective = scoreboard.registerNewObjective("lg", "dummy",
                ChatColor.GREEN.toString() + ChatColor.BOLD + "Loups-Garous");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        return objective;
    }

    public void removePlayer(LGPlayer player) {
        player.minecraftNoContext(this::removePlayer);
    }

    private ImmutableSet<ScoreboardComponent> getScoreboardComponents() {
        return orchestrator.getGameBox().contents(LGExtensionPoints.SCOREBOARD_COMPONENTS);
    }
}
