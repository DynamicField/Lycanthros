package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.CompositionValidator;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.CompositionValidatorHandler;
import com.github.jeuxjeux20.loupsgarous.cards.revealers.CardRevealer;
import com.github.jeuxjeux20.loupsgarous.cards.revealers.CardRevealerHandler;
import com.github.jeuxjeux20.loupsgarous.descriptor.Descriptor;
import com.github.jeuxjeux20.loupsgarous.descriptor.DescriptorProcessor;
import com.github.jeuxjeux20.loupsgarous.inventory.InventoryItem;
import com.github.jeuxjeux20.loupsgarous.phases.RunnableLGPhase;
import com.github.jeuxjeux20.loupsgarous.phases.dusk.DuskAction;
import com.github.jeuxjeux20.loupsgarous.phases.overrides.PhaseOverride;
import com.github.jeuxjeux20.loupsgarous.scoreboard.ScoreboardComponent;
import com.github.jeuxjeux20.loupsgarous.tags.revealers.TagRevealer;
import com.github.jeuxjeux20.loupsgarous.tags.revealers.TagRevealerHandler;
import com.github.jeuxjeux20.loupsgarous.teams.revealers.TeamRevealer;
import com.github.jeuxjeux20.loupsgarous.teams.revealers.TeamRevealerHandler;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public final class LGExtensionPoints {
    public static final ExtensionPoint<Class<? extends RunnableLGPhase>> PHASES =
            new ExtensionPoint<>("phases", new TypeToken<Class<? extends RunnableLGPhase>>() {});

    public static final ExtensionPoint<PhaseOverride> PHASE_OVERRIDES =
            new ExtensionPoint<>("phase_overrides", PhaseOverride.class);

    public static final ExtensionPoint<Class<? extends DuskAction>> DUSK_ACTIONS =
            new ExtensionPoint<>("dusk_actions", new TypeToken<Class<? extends DuskAction>>() {});

    public static final ExtensionPoint<LGCard> CARDS =
            new ExtensionPoint<>("cards", LGCard.class);

    public static final HandledExtensionPoint<TeamRevealer, TeamRevealerHandler> TEAM_REVEALERS =
            new HandledExtensionPoint<>("team_revealers", TeamRevealer.class, TeamRevealerHandler.class);

    public static final HandledExtensionPoint<CardRevealer, CardRevealerHandler> CARD_REVEALERS =
            new HandledExtensionPoint<>("card_revealers", CardRevealer.class, CardRevealerHandler.class);

    public static final HandledExtensionPoint<TagRevealer, TagRevealerHandler> TAG_REVEALERS =
            new HandledExtensionPoint<>("tag_revealers", TagRevealer.class, TagRevealerHandler.class);

    public static final HandledExtensionPoint<CompositionValidator, CompositionValidatorHandler> COMPOSITION_VALIDATORS =
            new HandledExtensionPoint<>("composition_validators", CompositionValidator.class, CompositionValidatorHandler.class);

    public static final ExtensionPoint<ScoreboardComponent> SCOREBOARD_COMPONENTS =
            new ExtensionPoint<>("scoreboard_components", ScoreboardComponent.class);

    public static final ExtensionPoint<InventoryItem> INVENTORY_ITEMS =
            new ExtensionPoint<>("inventory_items", InventoryItem.class);

    private static final LoadingCache<Class<? extends Descriptor<?>>, ExtensionPoint<DescriptorProcessor<?>>>
            DESCRIPTOR_PROCESSOR_CACHE =
            CacheBuilder.newBuilder().build(new DescriptorProcessorExtensionPointLoader());

    private LGExtensionPoints() {
    }

    @SuppressWarnings("unchecked")
    public static <D extends Descriptor<?>>
    ExtensionPoint<DescriptorProcessor<D>> descriptorProcessors(Class<D> descriptorClass) {
        return (ExtensionPoint<DescriptorProcessor<D>>) (Object)
                DESCRIPTOR_PROCESSOR_CACHE.getUnchecked(descriptorClass);
    }

    private static final class DescriptorProcessorExtensionPointLoader
            extends CacheLoader<Class<? extends Descriptor<?>>, ExtensionPoint<DescriptorProcessor<?>>> {

        @SuppressWarnings("unchecked")
        @Override
        public ExtensionPoint<DescriptorProcessor<?>> load(@NotNull Class<? extends Descriptor<?>> key) {
            return (ExtensionPoint<DescriptorProcessor<?>>) (Object)
                    create(key);
        }

        private <D extends Descriptor<?>> ExtensionPoint<DescriptorProcessor<D>>
        create(Class<D> descriptorClass) {
            String name = descriptorClass.getSimpleName() + "_processors";
            TypeToken<DescriptorProcessor<D>> type =
                    new TypeToken<DescriptorProcessor<D>>() {}
                            .where(new TypeParameter<D>() {}, descriptorClass);

            return new ExtensionPoint<>(name, type);
        }
    }
}
