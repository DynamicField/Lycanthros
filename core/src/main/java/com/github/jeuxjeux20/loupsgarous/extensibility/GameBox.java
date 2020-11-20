package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.event.GameEventHandler;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.relativesorting.ElementSorter;
import com.github.jeuxjeux20.relativesorting.OrderConstraints;
import com.github.jeuxjeux20.relativesorting.config.SortingConfiguration;
import com.github.jeuxjeux20.relativesorting.config.UnresolvableIdentifierHandling;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class GameBox implements Terminable {
    private static final SortingConfiguration SORTING_CONFIGURATION =
            SortingConfiguration.builder()
                    .unresolvableIdentifierHandling(UnresolvableIdentifierHandling.IGNORE)
                    .build();

    private final LGGameOrchestrator orchestrator;
    private final ModRegistry modRegistry;
    private final GameEventHandler eventHandler;

    private final Map<Mod, ModData> mods = new HashMap<>();
    private final Multimap<@Nullable Mod, Rule> rules = LinkedHashMultimap.create();
    private final Multimap<Rule, Extension<?>> extensionsByRule = LinkedHashMultimap.create();
    private final Multimap<ExtensionPoint<?>, Extension<?>> extensionsByPoint = LinkedHashMultimap.create();
    private final Multimap<ExtensionPoint<?>, Object> contents = LinkedListMultimap.create();

    private final ModRegistryListener modRegistryListener;

    private ImmutableMultimap<ExtensionPoint<?>, Object> preReportContents = ImmutableMultimap.of();
    private final Set<ExtensionPoint<?>> extensionPointsToSort = new HashSet<>();

    private final Subject<Change> changeSubject = PublishSubject.create();

    public GameBox(LGGameOrchestrator orchestrator,
                   ModRegistry modRegistry) {
        this.orchestrator = orchestrator;
        this.modRegistry = modRegistry;
        this.eventHandler = new GameEventHandler(orchestrator.getLoupsGarous());
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
    }

    @SuppressWarnings("unchecked")
    public <T> ImmutableList<T> contents(ExtensionPoint<T> extensionPoint) {
        return ImmutableList.copyOf((Collection<T>) contents.get(extensionPoint));
    }

    @SuppressWarnings("unchecked")
    public <T> ImmutableSet<Extension<T>> extensions(ExtensionPoint<T> extensionPoint) {
        return ImmutableSet.copyOf(
                (Collection<Extension<T>>) (Collection<?>) extensionsByPoint.get(extensionPoint)
        );
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
        ModData data = mods.computeIfAbsent(mod, ModData::new);

        if (data.enabled) {
            disable(mod);
        } else {
            enable(mod);
        }
    }

    public void configure(Mod mod, ConfigurationNode configuration) {
        orchestrator.logger().fine("Configuring mod " + mod);
        updateModData(mod, d -> d.configuration = configuration);
    }

    public @Nullable ModData getModData(Mod mod) {
        return mods.get(mod);
    }

    public ImmutableMap<Mod, ModData> getMods() {
        return ImmutableMap.copyOf(mods);
    }

    public <T extends Rule> Optional<T> getRule(Class<T> ruleClass) {
        return rules.values().stream()
                .filter(ruleClass::isInstance)
                .map(ruleClass::cast)
                .findAny();
    }

    public Observable<Change> updates() {
        return changeSubject;
    }

    private void updateModData(Mod mod, Consumer<ModData> modDataConsumer) {
        ModData data = mods.computeIfAbsent(mod, ModData::new);
        modDataConsumer.accept(data);

        updateMods(Collections.singleton(mod));
    }

    public void addMods(Collection<Mod> mods) {
        orchestrator.logger().fine("Adding mods: " + formatForLogging(mods));
        for (Mod mod : mods) {
            this.mods.put(mod, new ModData(mod));
        }
        updateMods(mods);
    }

    public void removeMods(Collection<Mod> mods) {
        orchestrator.logger().fine("Removing mods: " + formatForLogging(mods));
        for (Mod mod : mods) {
            this.mods.remove(mod);
        }
        updateMods(mods);
    }

    private void updateMods(Collection<Mod> mods) {
        long startTime = System.nanoTime();

        for (Mod mod : mods) {
            @Nullable ModData data = this.mods.get(mod);

            Rule[] presentModRules = rules.get(mod).toArray(new Rule[0]);

            if (data != null && data.enabled) {
                if (rules.containsKey(mod)) {
                    orchestrator.logger().fine("-> " + mod + ": removing old rules");

                    for (Rule rule : presentModRules) {
                        detachRule(rule);
                    }
                }
                orchestrator.logger().fine("-> " + mod + ": adding rules");

                for (Rule rule : mod.createRules(orchestrator, data.configuration)) {
                    attachRule(mod, rule);
                    activateRule(rule);
                }
            } else {
                if (presentModRules.length == 0) {
                    orchestrator.logger().fine("-> " + mod + ": no changes");
                } else {
                    orchestrator.logger().fine("-> " + mod + ": removing rules");
                }
                for (Rule rule : presentModRules) {
                    detachRule(rule);
                }
            }
        }

        long elapsedTime = System.nanoTime() - startTime;
        orchestrator.logger().finer("Mods update took " +
                                    TimeUnit.NANOSECONDS.toMicros(elapsedTime) + "µs");

        completeOperation();
    }

    void attachRule(@Nullable Mod mod, Rule rule) {
        if (rule.getState() != Rule.State.DETACHED) {
            return;
        }

        orchestrator.logger().fine("Attaching rule \"" + rule + '"');

        this.rules.put(mod, rule);

        // Setting the GameBox is required because we might add stuff
        // before the orchestrator's gameBox is initialized.
        rule.setGameBox(this);
        rule.setMod(mod);
        rule.setState(Rule.State.ATTACHED);
    }

    void detachRule(Rule rule) {
        if (rule.getState() == Rule.State.DETACHED) {
            return;
        }

        orchestrator.logger().fine("Detaching rule \"" + rule + '"');

        for (Map.Entry<Mod, Rule> entry : rules.entries()) {
            if (entry.getValue() == rule) {
                rules.remove(entry.getKey(), entry.getValue());
            }
        }

        if (rule.getState() == Rule.State.ACTIVATED) {
            deactivateRule(rule);
        }

        rule.setGameBox(null);
        rule.setMod(null);
        rule.setState(Rule.State.DETACHED);
    }

    void activateRule(Rule rule) {
        Preconditions.checkState(rule.getState() != Rule.State.DETACHED,
                "Cannot activate a detached rule.");
        if (rule.getState() == Rule.State.ACTIVATED) {
            return;
        }

        orchestrator.logger().fine("Activating rule \"" + rule + '"');

        addRuleExtensions(rule);
        rule.activate();
        enableRuleBehavior(rule);

        rule.setState(Rule.State.ACTIVATED);
    }

    void deactivateRule(Rule rule) {
        Preconditions.checkState(rule.getState() != Rule.State.DETACHED,
                "Cannot deactivate a detached rule.");
        if (rule.getState() == Rule.State.ATTACHED) {
            return;
        }

        orchestrator.logger().fine("Deactivating rule \"" + rule + '"');

        removeRuleExtensions(rule);
        rule.deactivate();
        disableRuleBehavior(rule);

        rule.setState(Rule.State.ATTACHED);
    }

    private void addRuleExtensions(Rule rule) {
        List<Extension<?>> ruleExtensions = rule.getExtensions();
        for (Extension<?> extension : ruleExtensions) {
            ExtensionPoint<?> extensionPoint = extension.getExtensionPoint();

            extensionPointsToSort.add(extensionPoint);

            extensionsByRule.get(rule).add(extension);
            extensionsByPoint.get(extensionPoint).add(extension);
            contents.get(extensionPoint).add(extension.getValue());
        }
    }

    private void removeRuleExtensions(Rule rule) {
        Extension<?>[] ruleExtensions = extensionsByRule.get(rule).toArray(new Extension<?>[0]);
        for (Extension<?> extension : ruleExtensions) {
            ExtensionPoint<?> extensionPoint = extension.getExtensionPoint();

            extensionsByRule.get(rule).remove(extension);
            extensionsByPoint.get(extensionPoint).remove(extension);
            contents.get(extensionPoint).remove(extension.getValue());
        }
    }

    private void enableRuleBehavior(Rule rule) {
        if (rule instanceof Listener) {
            eventHandler.register((Listener) rule);
        }
    }

    private void disableRuleBehavior(Rule rule) {
        if (rule instanceof Listener) {
            eventHandler.unregister((Listener) rule);
        }
    }

    void refreshRule(Rule rule) {
        if (rule.getState() == Rule.State.ACTIVATED) {
            removeRuleExtensions(rule);
            addRuleExtensions(rule);
        }
    }

    private boolean isReactiveToModChanges() {
        return orchestrator.allowsJoin();
    }

    private String formatForLogging(Collection<?> collection) {
        return "[" + collection.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", ")) + "]";
    }

    public String getContentsString() {
        StringBuilder builder = new StringBuilder();

        for (ExtensionPoint<?> extensionPoint : extensionsByPoint.keySet()) {
            builder.append(extensionPoint.getId()).append("\n");

            for (Extension<?> extension : extensionsByPoint.get(extensionPoint)) {
                builder.append("- ")
                        .append(formatExtension(extension))
                        .append("\n");
            }
        }

        return builder.toString();
    }

    private String formatExtension(Extension<?> extension) {
        StringBuilder stringBuilder = new StringBuilder();

        String identifier = extension.getExtensionMetadata().getIdentifier();
        if (identifier == null) {
            identifier = "<unnamed>";
        }

        stringBuilder.append(identifier)
                .append(": ")
                .append(extension.getValue())
        .append(" - Ordering: ");

        List<String> orderingAttributes = new ArrayList<>();
        OrderConstraints constraints = extension.getExtensionMetadata().getOrderConstraints();

        if (!constraints.getBefore().isEmpty()) {
            String beforeIds = constraints.getBefore().stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining(", "));

            orderingAttributes.add("before: [" + beforeIds + "]");
        }

        if (!constraints.getAfter().isEmpty()) {
            String afterIds = constraints.getAfter().stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining(", "));

            orderingAttributes.add("after: [" + afterIds + "]");
        }

        if (constraints.getPosition() != 0) {
            orderingAttributes.add("position: " + constraints.getPosition());
        }

        if (orderingAttributes.isEmpty()) {
            stringBuilder.append("<none>");
        } else {
            stringBuilder.append('{')
                    .append(String.join(", ", orderingAttributes))
                    .append('}');
        }

        return stringBuilder.toString();
    }

    void completeOperation() {
        for (ExtensionPoint<?> extensionPoint : extensionPointsToSort) {
            ElementSorter<Extension<?>> sorter =
                    new ElementSorter<>(Extension::getOrderedElement);

            List<Extension<?>> sorted =
                    sorter.sort(ImmutableList.copyOf(extensionsByPoint.get(extensionPoint)),
                            SORTING_CONFIGURATION);

            extensionsByPoint.removeAll(extensionPoint);
            contents.removeAll(extensionPoint);

            for (Extension<?> extension : sorted) {
                extensionsByPoint.put(extensionPoint, extension);
                contents.put(extensionPoint, extension.getValue());
            }
        }

        Change change = new Change(preReportContents, contents);
        if (!change.isEmpty()) {
            changeSubject.onNext(change);
        }

        preReportContents = ImmutableMultimap.copyOf(contents);
        extensionPointsToSort.clear();
    }

    @Override
    public void close() {
        modRegistry.removeListener(modRegistryListener);
        rules.values().forEach(this::detachRule);
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

        public boolean isEmpty() {
            for (Map.Entry<ExtensionPoint<?>, Diff<?>> entry : contentsDiff.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    return false;
                }
            }
            return true;
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

    public final class ModData {
        boolean enabled;
        ConfigurationNode configuration;

        private ModData(Mod mod) {
            this.enabled = orchestrator.allowsJoin() &&
                           mod.getDescriptor().isEnabledByDefault();
            this.configuration = mod.getDefaultConfiguration();
        }

        public boolean isEnabled() {
            return enabled;
        }

        public ConfigurationNode getConfiguration() {
            return configuration;
        }
    }
}
