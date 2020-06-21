package com.github.jeuxjeux20.loupsgarous.game;

import com.google.common.collect.ImmutableList;
import me.lucko.helper.metadata.AbstractMetadataRegistry;
import me.lucko.helper.metadata.MetadataRegistry;

final class LGMetadataRegistries {
    public static final MetadataRegistry<LGGameOrchestrator> GAME = new AbstractMetadataRegistry<>();
    public static final MetadataRegistry<LGPlayer> PLAYER = new AbstractMetadataRegistry<>();

    public static final ImmutableList<MetadataRegistry<?>> VALUES = ImmutableList.of(GAME, PLAYER);

    private LGMetadataRegistries() {
    }
}
