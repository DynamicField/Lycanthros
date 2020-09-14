package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.cards.*;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.MultipleTeamsCompositionValidator;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.PossibleCouplesCupidonCompositionValidator;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.UniqueCardCompositionValidator;
import com.github.jeuxjeux20.loupsgarous.cards.revealers.*;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatChannels;
import com.github.jeuxjeux20.loupsgarous.chat.PetiteFilleSpiesOnLoupsGarous;
import com.github.jeuxjeux20.loupsgarous.event.GameEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.MaireVoteOutcomeTransformer;
import com.github.jeuxjeux20.loupsgarous.inventory.EditLobbyItem;
import com.github.jeuxjeux20.loupsgarous.inventory.QuitGameItem;
import com.github.jeuxjeux20.loupsgarous.kill.LGKill;
import com.github.jeuxjeux20.loupsgarous.kill.causes.CouplePartnerKillCause;
import com.github.jeuxjeux20.loupsgarous.phases.*;
import com.github.jeuxjeux20.loupsgarous.phases.dusk.DuskPhase;
import com.github.jeuxjeux20.loupsgarous.phases.dusk.VoyanteDuskAction;
import com.github.jeuxjeux20.loupsgarous.powers.ChasseurPower;
import com.github.jeuxjeux20.loupsgarous.scoreboard.CompositionScoreboardComponent;
import com.github.jeuxjeux20.loupsgarous.scoreboard.CurrentVotesScoreboardComponent;
import com.github.jeuxjeux20.loupsgarous.scoreboard.LobbyOwnerScoreboardComponent;
import com.github.jeuxjeux20.loupsgarous.scoreboard.PlayersAliveScoreboardComponent;
import com.github.jeuxjeux20.loupsgarous.tags.revealers.MaireTagRevealer;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.teams.revealers.LoupsGarousTeamRevealer;
import com.github.jeuxjeux20.loupsgarous.winconditions.CoupleWinCondition;
import com.github.jeuxjeux20.loupsgarous.winconditions.EveryoneDeadWinCondition;
import com.github.jeuxjeux20.loupsgarous.winconditions.LoupsGarousWinCondition;
import com.github.jeuxjeux20.loupsgarous.winconditions.VillageWinCondition;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.*;
import static com.github.jeuxjeux20.loupsgarous.phases.RunnableLGPhase.createPhaseFactory;

@ModInfo(
        name = "Loups-Garous classique",
        hidden = true,
        enabledByDefault = true
)
public class ClassicGameMod extends Mod {
    private final EditLobbyItem editLobbyItem;

    @Inject
    ClassicGameMod(EditLobbyItem editLobbyItem) {
        this.editLobbyItem = editLobbyItem;
    }

    @Override
    public List<Rule> createRules(LGGameOrchestrator orchestrator, ConfigurationNode configuration) {
        return ImmutableList.of(
                new FundamentalsRule(orchestrator, editLobbyItem),
                new CupidonRule(orchestrator),
                new CoupleRule(orchestrator),
                new MaireRule(orchestrator),
                new PetiteFilleRule(orchestrator),
                new SorciereRule(orchestrator),
                new ChasseurRule(orchestrator),
                new VoyanteRule(orchestrator)
        );
    }

    public static class MaireRule extends Rule {
        public MaireRule(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public List<Extension<?>> getExtensions() {
            return ImmutableList.of(
                    extend(PHASES,
                            createPhaseFactory(MaireElectionPhase.class)),
                    extend(TAG_REVEALERS,
                            new MaireTagRevealer()),
                    extend(PLAYER_VOTE_OUTCOME_TRANSFORMERS,
                            new MaireVoteOutcomeTransformer())
            );
        }

        @Override
        public String getName() {
            return "Maire";
        }
    }

    public static class PetiteFilleRule extends CardRule {
        public PetiteFilleRule(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public LGCard getCard() {
            return PetiteFilleCard.INSTANCE;
        }

        @Override
        public List<Extension<?>> getOtherExtensions() {
            return ImmutableList.of(
                    extend(CHAT_CHANNEL_VIEW_TRANSFORMERS,
                            new PetiteFilleSpiesOnLoupsGarous())
            );
        }

        @Override
        public String getName() {
            return "Petite fille";
        }
    }

    public static class SorciereRule extends CardRule {
        public SorciereRule(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public LGCard getCard() {
            return SorciereCard.INSTANCE;
        }

        @Override
        public List<Extension<?>> getOtherExtensions() {
            return ImmutableList.of(
                    extend(PHASES,
                            createPhaseFactory(SorcierePotionPhase.class))
            );
        }

        @Override
        public String getName() {
            return "Sorcière";
        }
    }

    public static class ChasseurRule extends CardRule implements Listener {
        public ChasseurRule(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public LGCard getCard() {
            return ChasseurCard.INSTANCE;
        }

        @Override
        public String getName() {
            return "Chasseur";
        }

        @GameEvent(priority = EventPriority.HIGH)
        public void onLGKill(LGKillEvent event) {
            for (LGKill kill : event.getKills()) {
                LGPlayer victim = kill.getVictim();

                if (victim.powers().has(ChasseurPower.class) && victim.isPresent()) {
                    event.getOrchestrator().phases()
                            .insert(new ChasseurKillPhase.Factory(victim));
                }
            }
        }
    }

    public static class VoyanteRule extends CardRule {
        public VoyanteRule(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public LGCard getCard() {
            return VoyanteCard.INSTANCE;
        }

        @Override
        public List<Extension<?>> getOtherExtensions() {
            return ImmutableList.of(
                    extend(DUSK_ACTIONS,
                            VoyanteDuskAction::new),
                    extend(CARD_REVEALERS,
                            new VoyanteCardRevealer())
            );
        }

        @Override
        public String getName() {
            return "Voyante";
        }
    }

    public static class CupidonRule extends CardRule {
        public CupidonRule(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public LGCard getCard() {
            return CupidonCard.INSTANCE;
        }

        @Override
        public List<Extension<?>> getOtherExtensions() {
            return ImmutableList.of(
                    extend(PHASES,
                            createPhaseFactory(CupidonCouplePhase.class))
            );
        }

        @Override
        public String getName() {
            return "Cupidon";
        }
    }

    public static class CoupleRule extends Rule {
        public CoupleRule(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public List<Extension<?>> getExtensions() {
            return ImmutableList.of(
                    extend(CARD_REVEALERS,
                            new CoupleCardRevealer()),
                    extend(COMPOSITION_VALIDATORS,
                            new PossibleCouplesCupidonCompositionValidator()),
                    extend(WIN_CONDITIONS,
                            new CoupleWinCondition())
            );
        }

        @Override
        public String getName() {
            return "Couple";
        }

        @GameEvent
        public void onLGKill(LGKillEvent event) {
            for (LGKill kill : event.getKills()) {
                LGPlayer whoDied = kill.getVictim();

                Optional<LGTeam> coupleTeam = whoDied.teams().get().stream()
                        .filter(LGTeams::isCouple)
                        .findFirst();

                coupleTeam.ifPresent(team -> {
                    Stream<LGPlayer> partners = event.getOrchestrator().getAlivePlayers()
                            .filter(x -> x.teams().get().contains(team));

                    partners.forEach(partner -> killPartner(partner, whoDied));
                });
            }
        }

        private void killPartner(LGPlayer partner, LGPlayer me) {
            partner.die(new CouplePartnerKillCause(me));
        }
    }

    public static class FundamentalsRule extends Rule {
        private final EditLobbyItem editLobbyItem;

        public FundamentalsRule(LGGameOrchestrator orchestrator, EditLobbyItem editLobbyItem) {
            super(orchestrator);
            this.editLobbyItem = editLobbyItem;
        }

        @Override
        public List<Extension<?>> getExtensions() {
            return ImmutableList.of(
                    extend(PHASES,
                            createPhaseFactory(DuskPhase.class),
                            createPhaseFactory(LoupGarouVotePhase.class),
                            createPhaseFactory(NextTimeOfDayPhase.class),
                            createPhaseFactory(RevealAllKillsPhase.class),
                            createPhaseFactory(VillageVotePhase.class)),
                    extend(CARDS,
                            LoupGarouCard.INSTANCE,
                            VillageoisCard.INSTANCE),
                    extend(TEAM_REVEALERS,
                            new LoupsGarousTeamRevealer()),
                    extend(CARD_REVEALERS,
                            new SelfCardRevealer(),
                            new GameEndedCardRevealer(),
                            new PlayerDeadCardRevealer()),
                    extend(COMPOSITION_VALIDATORS,
                            new MultipleTeamsCompositionValidator(),
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
                            new LoupsGarousWinCondition(),
                            new VillageWinCondition()),
                    extend(CHAT_CHANNELS,
                            LGChatChannels.DAY,
                            LGChatChannels.DEAD,
                            LGChatChannels.LOUPS_GAROUS)
            );
        }

        @Override
        public String getName() {
            return "Fundamentals";
        }
    }
}

