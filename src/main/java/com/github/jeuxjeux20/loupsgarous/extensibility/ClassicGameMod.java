package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.cards.*;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.MultipleTeamsCompositionValidator;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.PossibleCouplesCupidonCompositionValidator;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.UniqueCardCompositionValidator;
import com.github.jeuxjeux20.loupsgarous.cards.revealers.*;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatChannels;
import com.github.jeuxjeux20.loupsgarous.chat.PetiteFilleSpiesOnLoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.MaireVoteOutcomeTransformer;
import com.github.jeuxjeux20.loupsgarous.inventory.EditLobbyItem;
import com.github.jeuxjeux20.loupsgarous.inventory.QuitGameItem;
import com.github.jeuxjeux20.loupsgarous.phases.*;
import com.github.jeuxjeux20.loupsgarous.phases.dusk.DuskPhase;
import com.github.jeuxjeux20.loupsgarous.phases.dusk.VoyanteDuskAction;
import com.github.jeuxjeux20.loupsgarous.scoreboard.CompositionScoreboardComponent;
import com.github.jeuxjeux20.loupsgarous.scoreboard.CurrentVotesScoreboardComponent;
import com.github.jeuxjeux20.loupsgarous.scoreboard.LobbyOwnerScoreboardComponent;
import com.github.jeuxjeux20.loupsgarous.scoreboard.PlayersAliveScoreboardComponent;
import com.github.jeuxjeux20.loupsgarous.tags.revealers.MaireTagRevealer;
import com.github.jeuxjeux20.loupsgarous.teams.revealers.LoupsGarousTeamRevealer;
import com.github.jeuxjeux20.loupsgarous.winconditions.CoupleWinCondition;
import com.github.jeuxjeux20.loupsgarous.winconditions.EveryoneDeadWinCondition;
import com.github.jeuxjeux20.loupsgarous.winconditions.LoupsGarousWinCondition;
import com.github.jeuxjeux20.loupsgarous.winconditions.VillageWinCondition;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.*;
import static com.github.jeuxjeux20.loupsgarous.phases.RunnableLGPhase.createPhaseFactory;

@ModInfo(
        name = "Loups-Garous classique",
        hidden = true,
        enabledByDefault = true
)
public class ClassicGameMod extends AbstractMod {
    private final EditLobbyItem editLobbyItem;

    @Inject
    ClassicGameMod(EditLobbyItem editLobbyItem) {
        this.editLobbyItem = editLobbyItem;
    }

    @Override
    public List<Rule> createRules(LGGameOrchestrator orchestrator, ConfigurationNode configuration) {
        return ImmutableList.of(
                new FundamentalsRule(orchestrator, editLobbyItem)
        );
    }

    private static class FundamentalsRule extends AbstractRule {
        private final EditLobbyItem editLobbyItem;

        public FundamentalsRule(LGGameOrchestrator orchestrator, EditLobbyItem editLobbyItem) {
            super(orchestrator);
            this.editLobbyItem = editLobbyItem;
        }

        @Override
        public List<Extension<?>> getExtensions() {
            return ImmutableList.of(
                    extend(PHASES,
                           createPhaseFactory(CupidonCouplePhase.class),
                           createPhaseFactory(DuskPhase.class),
                           createPhaseFactory(LoupGarouVotePhase.class),
                           createPhaseFactory(SorcierePotionPhase.class),
                           createPhaseFactory(NextTimeOfDayPhase.class),
                           createPhaseFactory(RevealAllKillsPhase.class),
                           createPhaseFactory(MaireElectionPhase.class),
                           createPhaseFactory(VillageVotePhase.class)),
                    extend(DUSK_ACTIONS,
                            VoyanteDuskAction::new),
                    extend(CARDS,
                            ChasseurCard.INSTANCE,
                            CupidonCard.INSTANCE,
                            LoupGarouCard.INSTANCE,
                            PetiteFilleCard.INSTANCE,
                            SorciereCard.INSTANCE,
                            VillageoisCard.INSTANCE,
                            VoyanteCard.INSTANCE),
                    extend(TEAM_REVEALERS,
                            new LoupsGarousTeamRevealer()),
                    extend(CARD_REVEALERS,
                            new SelfCardRevealer(),
                            new GameEndedCardRevealer(),
                            new PlayerDeadCardRevealer(),
                            new CoupleCardRevealer(),
                            new VoyanteCardRevealer()),
                    extend(TAG_REVEALERS,
                            new MaireTagRevealer()),
                    extend(COMPOSITION_VALIDATORS,
                            new MultipleTeamsCompositionValidator(),
                            new PossibleCouplesCupidonCompositionValidator(),
                            new UniqueCardCompositionValidator()),
                    extend(SCOREBOARD_COMPONENTS,
                            new LobbyOwnerScoreboardComponent(),
                            new PlayersAliveScoreboardComponent(),
                            new CompositionScoreboardComponent(),
                            new CurrentVotesScoreboardComponent()),
                    extend(INVENTORY_ITEMS,
                            editLobbyItem,
                            new QuitGameItem()),
                    extend(WIN_CONDITIONS,
                            new EveryoneDeadWinCondition(),
                            new CoupleWinCondition(),
                            new LoupsGarousWinCondition(),
                            new VillageWinCondition()),
                    extend(CHAT_CHANNELS,
                            LGChatChannels.DAY,
                            LGChatChannels.DEAD,
                            LGChatChannels.LOUPS_GAROUS),
                    extend(CHAT_CHANNEL_VIEW_TRANSFORMERS,
                            new PetiteFilleSpiesOnLoupsGarous()),
                    extend(PLAYER_VOTE_OUTCOME_TRANSFORMERS,
                            new MaireVoteOutcomeTransformer())
            );
        }

        @Override
        public String getName() {
            return "Fundamentals";
        }
    }
}
