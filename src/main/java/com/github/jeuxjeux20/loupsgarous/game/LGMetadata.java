package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.event.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerQuitEvent;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.metadata.MetadataMap;
import me.lucko.helper.metadata.MetadataRegistry;
import org.bukkit.event.EventPriority;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class LGMetadata {
    private static final AtomicBoolean SETUP = new AtomicBoolean(false);

    private LGMetadata() {
    }

    private static void ensureSetup() {
        if (SETUP.get()) {
            return;
        }

        if (!SETUP.getAndSet(true)) {
            Events.subscribe(LGGameDeletedEvent.class, EventPriority.MONITOR)
                    .handler(e -> {
                        LGGameOrchestrator orchestrator = e.getOrchestrator();

                        games().remove(orchestrator);
                        for (LGPlayer player : orchestrator.game().getPlayers()) {
                            players().get(player).ifPresent(MetadataMap::clear);
                        }
                    });

            Events.subscribe(LGPlayerQuitEvent.class, EventPriority.MONITOR)
                    .handler(e -> players().remove(e.getLGPlayer()));

            Schedulers.builder()
                    .async()
                    .afterAndEvery(1, TimeUnit.MINUTES)
                    .run(() -> {
                        for (MetadataRegistry<?> registry : LGMetadataRegistries.VALUES) {
                            registry.cleanup();
                        }
                    });
        }
    }

    public static MetadataRegistry<LGGameOrchestrator> games() {
        ensureSetup();
        return LGMetadataRegistries.GAME;
    }

    public static MetadataMap provideForGame(LGGameOrchestrator orchestrator) {
        return games().provide(orchestrator);
    }

    public static MetadataRegistry<LGPlayer> players() {
        ensureSetup();
        return LGMetadataRegistries.PLAYER;
    }

    public static MetadataMap provideForPlayer(LGPlayer player) {
        return players().provide(player);
    }
}
