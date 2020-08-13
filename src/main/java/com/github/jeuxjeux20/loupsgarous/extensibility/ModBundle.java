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

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ModBundle bundle) {
        return new Builder(bundle);
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

    public ImmutableList<Extension<?>> createEnabledModsExtensions() {
        ImmutableList.Builder<Extension<?>> builder = ImmutableList.builder();

        for (Mod mod : getEnabledMods()) {
            ModData modData = getModData(mod);
            assert modData != null;

            List<Extension<?>> extensions = mod.createExtensions(modData.getConfiguration());
            builder.addAll(extensions);
        }

        return builder.build();
    }

    public ModBundle transform(Consumer<Builder> builderConsumer) {
        Builder builder = builder();
        builderConsumer.accept(builder);

        return builder.build();
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

    public static class Builder {
        private final Map<Mod, ModData> mods;

        public Builder(ModBundle bundle) {
            this.mods = new HashMap<>(bundle.getMods());
        }

        public Builder() {
            this.mods = new HashMap<>();
        }

        public Builder put(Mod mod, ModData data) {
            mods.put(mod, data);
            return this;
        }

        public Builder put(Mod mod, boolean enabled, ConfigurationNode configurationNode) {
            return put(mod, new ModData(enabled, configurationNode));
        }

        public Builder put(Mod mod, boolean enabled) {
            return put(mod, enabled, mod.getDefaultConfiguration());
        }

        public Builder put(Mod mod) {
            return put(mod, true);
        }

        public Builder remove(Mod mod) {
            mods.remove(mod);
            return this;
        }

        public ModBundle build() {
            return new ModBundle(mods);
        }
    }
}
