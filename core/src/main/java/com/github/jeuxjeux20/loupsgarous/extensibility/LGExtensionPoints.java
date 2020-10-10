package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.CompositionValidator;
import com.github.jeuxjeux20.loupsgarous.chat.ChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcomeTransformer;
import com.github.jeuxjeux20.loupsgarous.inventory.InventoryItem;
import com.github.jeuxjeux20.loupsgarous.mechanic.MechanicModifier;
import com.github.jeuxjeux20.loupsgarous.mechanic.MechanicModifierSource;
import com.github.jeuxjeux20.loupsgarous.phases.RunnableLGPhase;
import com.github.jeuxjeux20.loupsgarous.phases.dusk.DuskAction;
import com.github.jeuxjeux20.loupsgarous.scoreboard.ScoreboardComponent;
import com.github.jeuxjeux20.loupsgarous.winconditions.WinCondition;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public final class LGExtensionPoints {
    private static final LoadingCache<Class<?>, ExtensionPoint<?>>
            VOTE_OUTCOME_TRANSFORMER_CACHE =
            CacheBuilder.newBuilder()
                    .build(new ClassExtensionPointCacheLoader<>("vote_outcome_transformers"));

    public static final ExtensionPoint<ContentFactory<? extends RunnableLGPhase>> PHASES =
            new ExtensionPoint<>("phases");

    public static final ExtensionPoint<ContentFactory<? extends DuskAction>> DUSK_ACTIONS =
            new ExtensionPoint<>("dusk_actions");

    public static final ExtensionPoint<LGCard> CARDS =
            new ExtensionPoint<>("cards");

    public static final ExtensionPoint<CompositionValidator> COMPOSITION_VALIDATORS =
            new ExtensionPoint<>("composition_validators");

    public static final ExtensionPoint<ScoreboardComponent> SCOREBOARD_COMPONENTS =
            new ExtensionPoint<>("scoreboard_components");

    public static final ExtensionPoint<InventoryItem> INVENTORY_ITEMS =
            new ExtensionPoint<>("inventory_items");

    public static final ExtensionPoint<WinCondition> WIN_CONDITIONS =
            new ExtensionPoint<>("win_conditions");

    public static final ExtensionPoint<ChatChannel> CHAT_CHANNELS =
            new ExtensionPoint<>("chat_channels");

    public static final ExtensionPoint<MechanicModifier> MECHANIC_MODIFIERS =
            new ExtensionPoint<>("mechanic_modifiers");

    public static final ExtensionPoint<MechanicModifierSource> MECHANIC_MODIFIER_SOURCES =
            new ExtensionPoint<>("mechanic_modifier_sources");

    public static ExtensionPoint<VoteOutcomeTransformer<LGPlayer>> PLAYER_VOTE_OUTCOME_TRANSFORMERS =
            voteOutcomeTransformers(LGPlayer.class);


    private LGExtensionPoints() {
    }

    @SuppressWarnings("unchecked")
    public static <D>
    ExtensionPoint<VoteOutcomeTransformer<D>> voteOutcomeTransformers(Class<D> candidateClass) {
        return (ExtensionPoint<VoteOutcomeTransformer<D>>)
                VOTE_OUTCOME_TRANSFORMER_CACHE.getUnchecked(candidateClass);
    }
}
