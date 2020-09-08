package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.extensibility.rule.Rule;
import com.github.jeuxjeux20.loupsgarous.extensibility.rule.RuleListener;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.relativesorting.ElementSorter;
import com.github.jeuxjeux20.relativesorting.OrderedElement;
import com.github.jeuxjeux20.relativesorting.OrderedElementTransformer;
import com.github.jeuxjeux20.relativesorting.config.SortingConfiguration;
import com.github.jeuxjeux20.relativesorting.config.UnresolvableClassHandling;
import com.google.common.collect.*;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class GameBox implements Terminable {
    private static final SortingConfiguration SORTING_CONFIGURATION =
            SortingConfiguration.builder()
                    .unresolvableClassHandling(UnresolvableClassHandling.IGNORE)
                    .build();

    private final LGGameOrchestrator orchestrator;
    private final ModRegistry modRegistry;

    private final Map<Mod, ModData> mods = new HashMap<>();
    private final Multimap<Mod, Rule> rules = LinkedHashMultimap.create();
    private final Multimap<ExtensionPoint<?>, Object> contents = LinkedHashMultimap.create();
    private final Map<HandledExtensionPoint<?, ?>, ExtensionPointHandler> handlers = new HashMap<>();

    private final Map<Rule, RuleListener> ruleListeners = new HashMap<>();
    private final ModRegistryListener modRegistryListener;

    private final Subject<Change> changeSubject = PublishSubject.create();

    public GameBox(LGGameOrchestrator orchestrator, ModRegistry modRegistry) {
        this.orchestrator = orchestrator;
        this.modRegistry = modRegistry;
        this.modRegistryListener = new ModRegistryListener() {
            @Override
            public void onModRemoved(Mod mod) {
                if (isReactiveToModChanges()) {
                    removeMods(Collections.singleton(mod));
                }
            }

            @Override
            public void onModAdded(Mod mod) {
                if (isReactiveToModChanges()) {
                    addMods(Collections.singleton(mod));
                }
            }
        };

        modRegistry.addListener(modRegistryListener);

        addMods(modRegistry.getMods());
    }

    @SuppressWarnings("unchecked")
    public <T> ImmutableSet<T> contents(ExtensionPoint<T> extensionPoint) {
        return ImmutableSet.copyOf((Collection<T>) contents.get(extensionPoint));
    }

    @SuppressWarnings("unchecked")
    public <H extends ExtensionPointHandler> H handler(HandledExtensionPoint<?, H> extensionPoint) {
        return (H) handlers.computeIfAbsent(extensionPoint,
                k -> orchestrator.resolve(k.getHandlerClass()));
    }

    public void enable(Mod mod) {
        orchestrator.logger().fine("Enabling mod " + mod);
        updateModData(mod, d -> d.enabled = true);
    }

    public void disable(Mod mod) {
        orchestrator.logger().fine("Disabling mod " + mod);
        updateModData(mod, d -> d.enabled = false);
    }

    public void toggle(Mod mod) {
        orchestrator.logger().fine("Toggling mod " + mod);
        updateModData(mod, d -> d.enabled = !d.enabled);
    }

    public void configure(Mod mod, ConfigurationNode configuration) {
        orchestrator.logger().fine("Configuring mod " + mod);
        updateModData(mod, d -> d.configuration = configuration);
    }

    public Observable<Change> onChange() {
        return changeSubject;
    }

    private void updateModData(Mod mod, Consumer<ModData> modDataConsumer) {
        ModData data = mods.computeIfAbsent(mod, ModData::new);
        modDataConsumer.accept(data);

        updateMods(Collections.singleton(mod));
    }

    private void addMods(Collection<Mod> mods) {
        orchestrator.logger().fine("Adding mods: " + formatForLogging(mods));
        for (Mod mod : mods) {
            this.mods.put(mod, new ModData(mod));
        }
        updateMods(mods);
    }

    private void removeMods(Collection<Mod> mods) {
        orchestrator.logger().fine("Removing mods: " + formatForLogging(mods));
        for (Mod mod : mods) {
            this.mods.remove(mod);
        }
        updateMods(mods);
    }

    private void updateMods(Collection<Mod> mods) {
        orchestrator.logger().fine("Updating mod rules... ");

        Multimap<Mod, Rule> rulesToAdd = LinkedHashMultimap.create();
        Multimap<Mod, Rule> rulesToRemove = HashMultimap.create();
        for (Mod mod : mods) {
            @Nullable ModData data = this.mods.get(mod);

            Collection<Rule> presentModRules = rules.get(mod);

            if (data != null && data.enabled) {
                if (rules.containsKey(mod)) {
                    orchestrator.logger().fine("-> " + mod + ": refreshing rules");

                    rulesToRemove.putAll(mod, presentModRules);
                } else {
                    orchestrator.logger().fine("-> " + mod + ": adding rules");
                }

                for (Rule rule : mod.createRules(orchestrator, data.configuration)) {
                    rulesToAdd.put(mod, rule);
                }
            } else {
                if (presentModRules.isEmpty()) {
                    orchestrator.logger().fine("-> " + mod + ": no changes");
                } else {
                    orchestrator.logger().fine("-> " + mod + ": removing rules");
                }
                rulesToRemove.putAll(mod, presentModRules);
            }
        }

        notifyRecordedChanges(() -> {
            addRules(rulesToAdd);
            removeRules(rulesToRemove);
        });

        orchestrator.logger().fine("Rules added: " + formatRulesName(rulesToAdd.values()));
        orchestrator.logger().fine("Rules removed: " + formatRulesName(rulesToRemove.values()));
    }

    private void addRules(Multimap<Mod, Rule> rules) {
        this.rules.putAll(rules);

        for (Rule rule : rules.values()) {
            if (rule.isEnabledByDefault()) {
                rule.enable();
            } else {
                rule.disable();
            }

            RuleListener ruleListener = new ExtensionUpdateRuleListener(rule);
            rule.addListener(ruleListener);
            ruleListeners.put(rule, ruleListener);
        }

        updateRules(rules.values());
    }

    private void removeRules(Multimap<Mod, Rule> rules) {
        for (Map.Entry<Mod, Rule> entry : rules.entries()) {
            this.rules.remove(entry.getKey(), entry.getValue());
        }

        for (Rule rule : rules.values()) {
            RuleListener listener = ruleListeners.remove(rule);
            if (listener != null) {
                rule.removeListener(listener);
            }
        }

        updateRules(rules.values());
    }

    private void updateRules(Collection<Rule> rules) {
        activateRules(
                rules.stream().filter(this::isActivationCandidate).collect(Collectors.toList())
        );
        deactivateRules(
                rules.stream().filter(x -> !isActivationCandidate(x)).collect(Collectors.toList())
        );
    }

    private void activateRules(Collection<Rule> addedRules) {
        Set<ExtensionPoint<?>> modifiedExtensionPoints = new HashSet<>();
        for (Rule rule : addedRules) {
            orchestrator.logger().fine("Activating rule \"" + rule.getName() + '"');

            List<Extension<?>> ruleExtensions = rule.getExtensions();
            for (Extension<?> extension : ruleExtensions) {
                ExtensionPoint<?> extensionPoint = extension.getExtensionPoint();

                modifiedExtensionPoints.add(extensionPoint);
                contents.get(extensionPoint).addAll(extension.getContents());
            }
        }

        for (ExtensionPoint<?> modifiedExtensionPoint : modifiedExtensionPoints) {
            ElementSorter<Object> sorter =
                    new ElementSorter<>(this::createOrderedElement);

            List<Object> sorted =
                    sorter.sort(ImmutableList.copyOf(contents.get(modifiedExtensionPoint)),
                            SORTING_CONFIGURATION);

            contents.replaceValues(modifiedExtensionPoint, sorted);
        }
    }

    private void deactivateRules(Collection<Rule> removedRules) {
        for (Rule rule : removedRules) {
            orchestrator.logger().fine("Deactivating rule \"" + rule.getName() + '"');
            List<Extension<?>> ruleExtensions = rule.getExtensions();
            for (Extension<?> extension : ruleExtensions) {
                ExtensionPoint<?> extensionPoint = extension.getExtensionPoint();

                contents.get(extensionPoint).removeAll(extension.getContents());
            }
        }
    }

    private boolean isActivationCandidate(Rule rule) {
        return rule.isEnabled() && rules.containsValue(rule);
    }

    private void notifyRecordedChanges(Runnable runnable) {
        ImmutableMultimap<ExtensionPoint<?>, Object> contentsSnapshot =
                ImmutableMultimap.copyOf(contents);

        runnable.run();

        Change change = new Change(contentsSnapshot, contents);
        changeSubject.onNext(change);
    }

    private OrderedElement<Object> createOrderedElement(Object x) {
        OrderedElement<Object> element = OrderedElement.fromType(x.getClass(), x);
        OrderTransformer annotation = x.getClass().getAnnotation(OrderTransformer.class);
        if (annotation != null) {
            try {
                Constructor<? extends OrderedElementTransformer> constructor =
                        annotation.value().getDeclaredConstructor();
                constructor.setAccessible(true);

                OrderedElementTransformer transformer = constructor.newInstance();
                element = transformer.transform(element);
            } catch (Exception e) {
                orchestrator.logger().log(Level.WARNING, "Failed to execute OrderTransformer.", e);
            }
        }
        return element;
    }

    private boolean isReactiveToModChanges() {
        return orchestrator.allowsJoin();
    }

    private String formatForLogging(Collection<?> collection) {
        return "[" + collection.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", ")) + "]";
    }

    private String formatRulesName(Collection<Rule> rules) {
        return "[" + rules.stream()
                .map(Rule::getName)
                .collect(Collectors.joining(", ")) + "]";
    }

    public String getContentsString() {
        StringBuilder builder = new StringBuilder();

        for (ExtensionPoint<?> extensionPoint : contents.keySet()) {
            builder.append(extensionPoint.getId()).append("\n");
            for (Object item : contents.get(extensionPoint)) {
                builder.append("- ")
                        .append(item)
                        .append("\n");
            }
        }

        return builder.toString();
    }

    @Override
    public void close() {
        modRegistry.removeListener(modRegistryListener);
        ruleListeners.forEach(Rule::removeListener);
        ruleListeners.clear();
        rules.values().forEach(Rule::disable);
    }

    public static class Change {
        private final ImmutableMap<ExtensionPoint<?>, Diff<?>> contentsDiff;

        public Change(Multimap<ExtensionPoint<?>, Object> contentsBefore,
                      Multimap<ExtensionPoint<?>, Object> contentsAfter) {
            Map<ExtensionPoint<?>, Diff<?>> diffMap = new HashMap<>();

            for (ExtensionPoint<?> extensionPoint : contentsAfter.keySet()) {
                Collection<Object> contentBefore = contentsBefore.get(extensionPoint);
                Collection<Object> contentAfter = contentsAfter.get(extensionPoint);

                diffMap.put(extensionPoint, new Diff<>(contentBefore, contentAfter));
            }

            this.contentsDiff = ImmutableMap.copyOf(diffMap);
        }

        @SuppressWarnings("unchecked")
        public <T> Diff<T> getContentsDiff(ExtensionPoint<T> extensionPoint) {
            return (Diff<T>) contentsDiff.getOrDefault(extensionPoint, Diff.EMPTY);
        }

        public static class Diff<T> {
            public static final Diff<?> EMPTY = new Diff<>();
            private final ImmutableSet<T> added;
            private final ImmutableSet<T> removed;

            private Diff() {
                this.added = ImmutableSet.of();
                this.removed = ImmutableSet.of();
            }

            public Diff(Collection<T> before, Collection<T> after) {
                ImmutableSet<T> beforeSet = ImmutableSet.copyOf(before);

                Set<T> added = new HashSet<>();
                Set<T> removed = new HashSet<>(before);

                for (T thing : after) {
                    if (!beforeSet.contains(thing)) {
                        added.add(thing);
                    }
                    removed.remove(thing);
                }

                this.added = ImmutableSet.copyOf(added);
                this.removed = ImmutableSet.copyOf(removed);
            }

            @SuppressWarnings("unchecked")
            public static <T> Diff<T> empty() {
                return (Diff<T>) EMPTY;
            }

            public ImmutableSet<T> getAdded() {
                return added;
            }

            public ImmutableSet<T> getRemoved() {
                return removed;
            }

            public boolean isEmpty() {
                return added.isEmpty() && removed.isEmpty();
            }
        }
    }

    private class ModData {
        boolean enabled;
        ConfigurationNode configuration;

        public ModData(Mod mod) {
            this.enabled = orchestrator.allowsJoin() &&
                           modRegistry.descriptors().get(mod.getClass()).isEnabledByDefault();
            this.configuration = mod.getDefaultConfiguration();
        }
    }

    private class ExtensionUpdateRuleListener implements RuleListener {
        final Rule rule;
        private final Collection<Rule> ruleCollection;

        private ExtensionUpdateRuleListener(Rule rule) {
            this.rule = rule;
            this.ruleCollection = Collections.singleton(rule);
        }

        @Override
        public void onEnable() {
            orchestrator.logger().fine("Rule \"" + rule.getName() + "\" has been enabled.");
            notifyRecordedChanges(() -> updateRules(ruleCollection));
        }

        @Override
        public void onDisable() {
            orchestrator.logger().fine("Rule \"" + rule.getName() + "\" has been disabled.");
            notifyRecordedChanges(() -> updateRules(ruleCollection));
        }
    }
}
