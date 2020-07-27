package com.github.jeuxjeux20.loupsgarous.scoreboard;

import com.github.jeuxjeux20.loupsgarous.HasTriggers;
import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.util.ClassArrayUtils;
import com.google.inject.Inject;
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
import java.util.Set;

@OrchestratorScoped
public class LGScoreboardManager extends AbstractOrchestratorComponent {
    private static final MetadataKey<Scoreboard> SCOREBOARD_KEY
            = MetadataKey.create("lg_scoreboard", Scoreboard.class);

    private final ScoreboardComponentRenderer componentRenderer;
    private final Set<ScoreboardComponent> components;

    @Inject
    LGScoreboardManager(LGGameOrchestrator orchestrator,
                        ScoreboardComponentRenderer componentRenderer,
                        Set<ScoreboardComponent> components) {
        super(orchestrator);
        this.componentRenderer = componentRenderer;
        this.components = components;

        registerEvents();
    }

    private void registerEvents() {
        Class<? extends LGEvent>[] classes =
                ClassArrayUtils.merge(components.stream().map(HasTriggers::getUpdateTriggers));

        Events.merge(LGEvent.class, classes) // Safe because of getUpdateTriggers().
                .filter(orchestrator::isMyEvent)
                .handler(e -> {
                    for (LGPlayer player : e.getGame().getPlayers()) {
                        updatePlayer(player);
                    }
                })
                .bindWith(this);

        Events.subscribe(LGPlayerJoinEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> updatePlayer(e.getLGPlayer()))
                .bindWith(this);

        Events.subscribe(LGPlayerQuitEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> removePlayer(e.getLGPlayer()))
                .bindWith(this);
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

        componentRenderer.renderObjective(objective, components, player, orchestrator);
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
}
