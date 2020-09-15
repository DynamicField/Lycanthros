package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.CompositionValidator;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.CompositionValidatorHandler;
import com.github.jeuxjeux20.loupsgarous.cards.revealers.CardRevealer;
import com.github.jeuxjeux20.loupsgarous.cards.revealers.CardRevealerHandler;
import com.github.jeuxjeux20.loupsgarous.chat.ChatChannel;
import com.github.jeuxjeux20.loupsgarous.chat.ChatChannelViewTransformer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcomeTransformer;
import com.github.jeuxjeux20.loupsgarous.inventory.InventoryItem;
import com.github.jeuxjeux20.loupsgarous.phases.RunnableLGPhase;
import com.github.jeuxjeux20.loupsgarous.phases.dusk.DuskAction;
import com.github.jeuxjeux20.loupsgarous.scoreboard.ScoreboardComponent;
import com.github.jeuxjeux20.loupsgarous.tags.revealers.TagRevealer;
import com.github.jeuxjeux20.loupsgarous.tags.revealers.TagRevealerHandler;
import com.github.jeuxjeux20.loupsgarous.teams.revealers.TeamRevealer;
import com.github.jeuxjeux20.loupsgarous.teams.revealers.TeamRevealerHandler;
import com.github.jeuxjeux20.loupsgarous.winconditions.WinCondition;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public final class LGExtensionPoints {
    private static final LoadingCache<Class<?>, ExtensionPoint<?>>
            VOTE_OUTCOME_TRANSFORMER_CACHE =
            CacheBuilder.newBuilder().build(new ClassExtensionPointCacheLoader<Object>("vote_outcome_transformers") {
                @Override
                protected <C> TypeToken<?> getExtensionPointType(Class<C> clazz) {
                    return new TypeToken<VoteOutcomeTransformer<C>>() {}
                            .where(new TypeParameter<C>() {}, clazz);
                }
            });

    public static final ExtensionPoint<SortableContentFactory<? extends RunnableLGPhase>> PHASES =
            new ExtensionPoint<>("phases",
                    new TypeToken<SortableContentFactory<? extends RunnableLGPhase>>() {});

    public static final ExtensionPoint<SortableContentFactory<? extends DuskAction>> DUSK_ACTIONS =
            new ExtensionPoint<>("dusk_actions",
                    new TypeToken<SortableContentFactory<? extends DuskAction>>() {});

    public static final ExtensionPoint<LGCard> CARDS =
            new ExtensionPoint<>("cards", LGCard.class);

    public static final HandledExtensionPoint<TeamRevealer, TeamRevealerHandler> TEAM_REVEALERS =
            new HandledExtensionPoint<>("team_revealers", TeamRevealer.class, TeamRevealerHandler.class);

    public static final HandledExtensionPoint<CardRevealer, CardRevealerHandler> CARD_REVEALERS =
            new HandledExtensionPoint<>("card_revealers", CardRevealer.class, CardRevealerHandler.class);

    public static final HandledExtensionPoint<TagRevealer, TagRevealerHandler> TAG_REVEALERS =
            new HandledExtensionPoint<>("tag_revealers", TagRevealer.class, TagRevealerHandler.class);

    public static final HandledExtensionPoint<CompositionValidator, CompositionValidatorHandler> COMPOSITION_VALIDATORS =
            new HandledExtensionPoint<>("composition_validators",
                    CompositionValidator.class, CompositionValidatorHandler.class);

    public static final ExtensionPoint<ScoreboardComponent> SCOREBOARD_COMPONENTS =
            new ExtensionPoint<>("scoreboard_components", ScoreboardComponent.class);

    public static final ExtensionPoint<InventoryItem> INVENTORY_ITEMS =
            new ExtensionPoint<>("inventory_items", InventoryItem.class);

    public static final ExtensionPoint<WinCondition> WIN_CONDITIONS =
            new ExtensionPoint<>("win_conditions", WinCondition.class);

    public static final ExtensionPoint<ChatChannelViewTransformer> CHAT_CHANNEL_VIEW_TRANSFORMERS =
            new ExtensionPoint<>("channel_properties_transformers", ChatChannelViewTransformer.class);

    public static final ExtensionPoint<ChatChannel> CHAT_CHANNELS =
            new ExtensionPoint<>("chat_channels", ChatChannel.class);

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
