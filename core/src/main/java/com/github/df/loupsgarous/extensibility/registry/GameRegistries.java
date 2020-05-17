package com.github.df.loupsgarous.extensibility.registry;

import com.github.df.loupsgarous.cards.LGCard;
import com.github.df.loupsgarous.cards.composition.validation.CompositionValidator;
import com.github.df.loupsgarous.chat.ChatChannel;
import com.github.df.loupsgarous.extensibility.ContentFactory;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.vote.outcome.VoteOutcomeTransformer;
import com.github.df.loupsgarous.inventory.InventoryItem;
import com.github.df.loupsgarous.mechanic.MechanicModifier;
import com.github.df.loupsgarous.phases.RunnablePhase;
import com.github.df.loupsgarous.phases.dusk.DuskAction;
import com.github.df.loupsgarous.scoreboard.ScoreboardComponent;
import com.github.df.loupsgarous.winconditions.WinCondition;

import static com.github.df.loupsgarous.extensibility.registry.GameRegistryKey.createOrdered;

public final class GameRegistries {
    private GameRegistries() {
        throw new UnsupportedOperationException("Why?");
    }

    public static final GameRegistryKey<ContentFactory<? extends RunnablePhase>> PHASES =
            createOrdered("phases");

    public static final GameRegistryKey<ContentFactory<? extends DuskAction>> DUSK_ACTIONS =
            createOrdered("dusk_actions");

    public static final GameRegistryKey<LGCard> CARDS =
            createOrdered("cards");

    public static final GameRegistryKey<CompositionValidator> COMPOSITION_VALIDATORS =
            createOrdered("composition_validators");

    public static final GameRegistryKey<ScoreboardComponent> SCOREBOARD_COMPONENTS =
            createOrdered("scoreboard_components");

    public static final GameRegistryKey<InventoryItem> INVENTORY_ITEMS =
            createOrdered("inventory_items");

    public static final GameRegistryKey<WinCondition> WIN_CONDITIONS =
            createOrdered("win_conditions");

    public static final GameRegistryKey<ChatChannel> CHAT_CHANNELS =
            createOrdered("chat_channels");

    public static final GameRegistryKey<MechanicModifier> MECHANIC_MODIFIERS =
            createOrdered("mechanic_modifiers");

    public static GameRegistryKey<VoteOutcomeTransformer<LGPlayer>> PLAYER_VOTE_OUTCOME_TRANSFORMERS =
            voteOutcomeTransformers(LGPlayer.class);

    public static <D>
    GameRegistryKey<VoteOutcomeTransformer<D>> voteOutcomeTransformers(Class<D> candidateClass) {
        return createOrdered("vote_outcome_transformers[" + candidateClass.getName() + "]");
    }
}
