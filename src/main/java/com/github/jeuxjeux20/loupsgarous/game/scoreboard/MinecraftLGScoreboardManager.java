package com.github.jeuxjeux20.loupsgarous.game.scoreboard;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.HasTriggers;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.events.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.util.ClassArrayUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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

@Singleton
class MinecraftLGScoreboardManager implements LGScoreboardManager {
    private static final MetadataKey<Scoreboard> SCOREBOARD_KEY
            = MetadataKey.create("lg_scoreboard", Scoreboard.class);

    private final ScoreboardComponentRenderer componentRenderer;
    private final Set<ScoreboardComponent> components;
    private final LoupsGarous plugin;

    private boolean hasEvents;

    @Inject
    MinecraftLGScoreboardManager(ScoreboardComponentRenderer componentRenderer,
                                 Set<ScoreboardComponent> components,
                                 LoupsGarous plugin) {
        this.componentRenderer = componentRenderer;
        this.components = components;
        this.plugin = plugin;
    }

    public void registerEvents() {
        if (hasEvents) return;

        Class<? extends LGEvent>[] classes = ClassArrayUtils.merge(components.stream().map(HasTriggers::getUpdateTriggers));

        Events.merge(LGEvent.class, classes) // Safe because of getUpdateTriggers().
                .handler(e -> {
                    for (LGPlayer player : e.getGame().getPlayers()) {
                        updatePlayer(player, e.getOrchestrator());
                    }
                })
                .bindWith(plugin);

        Events.subscribe(LGPlayerJoinEvent.class)
                .handler(e -> updatePlayer(e.getLGPlayer(), e.getOrchestrator()))
                .bindWith(plugin);

        Events.subscribe(LGPlayerQuitEvent.class)
                .handler(e -> removePlayer(e.getLGPlayer()))
                .bindWith(plugin);

        hasEvents = true;
    }

    @Override
    public void updatePlayer(LGPlayer player, LGGameOrchestrator orchestrator) {
        player.getMinecraftPlayer().ifPresent(minecraftPlayer -> {
            Scoreboard scoreboard = Metadata.provideForPlayer(minecraftPlayer).getOrPut(SCOREBOARD_KEY,
                    () -> Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());

            update(player, scoreboard, orchestrator);
            minecraftPlayer.setScoreboard(scoreboard);
        });
    }

    @Override
    public void removePlayer(Player player) {
        Metadata.provideForPlayer(player).get(SCOREBOARD_KEY).ifPresent(scoreboard -> {
            player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard());
            Metadata.provideForPlayer(player).remove(SCOREBOARD_KEY);
        });
    }

    private void update(LGPlayer player, Scoreboard scoreboard, LGGameOrchestrator orchestrator) {
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
}
