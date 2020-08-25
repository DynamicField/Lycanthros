package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ModBundle {
    private final ImmutableMap<Mod, ModData> mods;

    private @Nullable ImmutableSet<Mod> enabledMods;

    public ModBundle(Map<Mod, ModData> mods) {
        this.mods = ImmutableMap.copyOf(mods);
    }

    public ModBundle(Collection<Mod> mods) {
        this(mods.stream().collect(
                Collectors.toMap(Function.identity(), x -> ModData.fromMod(x, true))
        ));
    }

    public ImmutableMap<Mod, ModData> getMods() {
        return mods;
    }

    public @Nullable ModData getModData(Mod mod) {
        return mods.get(mod);
    }

    public ImmutableSet<Mod> getEnabledMods() {
        if (enabledMods == null) {
            enabledMods = mods.entrySet().stream()
                    .filter(e -> e.getValue().isEnabled())
                    .map(Map.Entry::getKey)
                    .collect(ImmutableSet.toImmutableSet());
        }

        return enabledMods;
    }

    public ImmutableList<Extension<?>> createExtensions() {
        ImmutableList.Builder<Extension<?>> builder = ImmutableList.builder();

        for (Mod mod : getEnabledMods()) {
            ModData modData = getModData(mod);
            assert modData != null;

            List<Extension<?>> extensions = mod.createExtensions(modData.getConfiguration());
            builder.addAll(extensions);
        }

        return builder.build();
    }

    public ModBundle transform(Consumer<Transformer> transformerConsumer) {
        Transformer transformer = new Transformer(this);
        transformerConsumer.accept(transformer);

        return transformer.createBundle();
    }

    public static final class Transformer {
        private final Map<Mod, ModData> mods;

        Transformer(ModBundle modBundle) {
            this.mods = new HashMap<>(modBundle.mods);
        }

        public void enable(Mod mod) {
            mods.compute(mod, (m, d) -> d == null ?
                    ModData.fromMod(m, true) :
                    d.withEnabled(true));
        }

        public void disable(Mod mod) {
            mods.compute(mod, (m, d) -> d == null ?
                    ModData.fromMod(m, false) :
                    d.withEnabled(false));
        }

        public void configureIfPresent(Mod mod, ConfigurationNode configuration) {
            mods.computeIfPresent(mod, (m, d) -> d.withConfiguration(configuration));
        }

        public void configure(Mod mod, ConfigurationNode configuration, boolean enabledIfAbsent) {
            mods.compute(mod, (m, d) -> d == null ?
                    new ModData(enabledIfAbsent, configuration) :
                    d.withConfiguration(configuration));
        }

        public ModBundle createBundle() {
            return new ModBundle(mods);
        }
    }

    public static class ModData {
        private final boolean enabled;
        private final ConfigurationNode configuration;

        public ModData(boolean enabled, ConfigurationNode configuration) {
            this.enabled = enabled;
            this.configuration = configuration;
        }

        public static ModData fromMod(Mod mod, boolean enabled) {
            return new ModData(enabled, mod.getDefaultConfiguration());
        }

        public boolean isEnabled() {
            return enabled;
        }

        public ModData withEnabled(boolean enabled) {
            return new ModData(enabled, this.configuration);
        }

        public ConfigurationNode getConfiguration() {
            return configuration.copy();
        }

        public ModData withConfiguration(ConfigurationNode configuration) {
            return new ModData(this.enabled, configuration);
        }
    }
}
