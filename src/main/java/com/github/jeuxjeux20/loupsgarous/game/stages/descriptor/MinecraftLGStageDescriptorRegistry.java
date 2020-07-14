package com.github.jeuxjeux20.loupsgarous.game.stages.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class MinecraftLGStageDescriptorRegistry implements LGStageDescriptorRegistry {
    private final LGStageDescriptorFinder descriptorFinder;

    private final LoadingCache<Class<? extends LGStage>, LGStageDescriptor> descriptorCache;

    @Inject
    MinecraftLGStageDescriptorRegistry(LGStageDescriptorFinder descriptorFinder) {
        this.descriptorFinder = descriptorFinder;
        this.descriptorCache = CacheBuilder.newBuilder().build(new DescriptorFinderLoader());
    }

    @Override
    public LGStageDescriptor get(Class<? extends LGStage> stageClass) {
        return descriptorCache.getUnchecked(stageClass);
    }

    @Override
    public void invalidate(Class<? extends LGStage> stageClass) {
        descriptorCache.invalidate(stageClass);
    }

    private final class DescriptorFinderLoader
            extends CacheLoader<Class<? extends LGStage>, LGStageDescriptor> {
        @Override
        public LGStageDescriptor load(@NotNull Class<? extends LGStage> key) {
            return descriptorFinder.find(key);
        }
    }
}
