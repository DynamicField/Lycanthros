package com.github.df.loupsgarous.scoreboard;

import com.github.df.loupsgarous.event.player.LGPlayerJoinEvent;
import com.github.df.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.df.loupsgarous.event.registry.RegistryChangeEvent;
import com.github.df.loupsgarous.extensibility.registry.GameRegistries;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.game.OrchestratorComponent;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
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

public class LGScoreboardManager extends OrchestratorComponent {
    private static final MetadataKey<Scoreboard> SCOREBOARD_KEY
            = MetadataKey.create("lg_scoreboard", Scoreboard.class);

    private final ScoreboardComponentRenderer componentRenderer;

    public LGScoreboardManager(LGGameOrchestrator orchestrator) {
        super(orchestrator);
        this.componentRenderer = new ScoreboardComponentRenderer();

        registerEvents();
    }

    private void registerEvents() {
        Schedulers.sync().runRepeating(this::updateAll, 0L, 10L)
                .bindWith(this);

        Events.subscribe(LGPlayerJoinEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> updatePlayer(e.getLGPlayer()))
                .bindWith(this);

        Events.subscribe(LGPlayerQuitEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> removePlayer(e.getLGPlayer()))
                .bindWith(this);

        Events.subscribe(RegistryChangeEvent.class)
                .filter(e -> e.getRegistry() == GameRegistries.INVENTORY_ITEMS.get(orchestrator))
                .handler(e -> updateAll())
                .bindWith(this);
    }

    public void updatePlayer(LGPlayer player) {
        player.minecraft(minecraftPlayer -> {
            Scoreboard scoreboard = Metadata.provideForPlayer(minecraftPlayer)
                    .getOrPut(SCOREBOARD_KEY,
                            () -> Objects.requireNonNull(Bukkit.getScoreboardManager())
                                    .getNewScoreboard());

            update(player, scoreboard);
            minecraftPlayer.setScoreboard(scoreboard);
        });
    }

    public void removePlayer(Player player) {
        Metadata.provideForPlayer(player).get(SCOREBOARD_KEY).ifPresent(scoreboard -> {
            player.setScoreboard(
                    Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard());
            Metadata.provideForPlayer(player).remove(SCOREBOARD_KEY);
        });
    }

    private void update(LGPlayer player, Scoreboard scoreboard) {
        Objective objective = recreateObjective(scoreboard);

        componentRenderer
                .renderObjective(objective, getScoreboardComponents(), player, orchestrator);
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
        return GameRegistries.SCOREBOARD_COMPONENTS.get(orchestrator).getValues();
    }
}
