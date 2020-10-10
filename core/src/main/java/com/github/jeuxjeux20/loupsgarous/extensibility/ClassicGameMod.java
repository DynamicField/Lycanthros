package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.cards.*;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.MultipleTeamsCompositionValidator;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.PossibleCouplesCupidonCompositionValidator;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.UniqueCardCompositionValidator;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatChannels;
import com.github.jeuxjeux20.loupsgarous.chat.PetiteFilleSpiesOnLoupsGarous;
import com.github.jeuxjeux20.loupsgarous.event.GameEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.MaireVoteOutcomeTransformer;
import com.github.jeuxjeux20.loupsgarous.inventory.EditLobbyItem;
import com.github.jeuxjeux20.loupsgarous.inventory.EditModsItem;
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
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.winconditions.CoupleWinCondition;
import com.github.jeuxjeux20.loupsgarous.winconditions.EveryoneDeadWinCondition;
import com.github.jeuxjeux20.loupsgarous.winconditions.LoupsGarousWinCondition;
import com.github.jeuxjeux20.loupsgarous.winconditions.VillageWinCondition;
import com.google.common.collect.ImmutableList;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.*;

@ModInfo(
        name = "Loups-Garous classique",
        hidden = true,
        enabledByDefault = true
)
public class ClassicGameMod extends Mod {
    @Override
    public List<Rule> createRules(LGGameOrchestrator orchestrator, ConfigurationNode configuration) {
        return ImmutableList.of(
                new FundamentalsRule(orchestrator),
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
            return Extension.listBuilder()
                    .extend(PHASES, b -> b
                            .add(MaireElectionPhase::new)
                            .id("MaireElection")
                            .locatedBefore("VillageVote")
                            .locatedAfter("RevealAllKills"))
                    .extendSingle(PLAYER_VOTE_OUTCOME_TRANSFORMERS,
                            new MaireVoteOutcomeTransformer())
                    .build();
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
            return Extension.listBuilder()
                    .extendSingle(MECHANIC_MODIFIERS,
                            new PetiteFilleSpiesOnLoupsGarous())
                    .build();
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
            return Extension.listBuilder()
                    .extend(PHASES, b -> b
                            .add(SorcierePotionPhase::new)
                            .id("SoricerePotion")
                            .locatedBefore("NextTimeOfDay")
                            .locatedAfter("LoupsGarousVote"))
                    .build();
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

        @GameEvent(priority = EventPriority.HIGH)
        public void onLGKill(LGKillEvent event) {
            for (LGKill kill : event.getKills()) {
                LGPlayer victim = kill.getVictim();

                if (victim.powers().has(ChasseurPower.class) && victim.isPresent()) {
                    PhaseProgram program = event.getOrchestrator().phases().getProgram();

                    if (program instanceof PhaseCycle) {
                        ChasseurKillPhase.Factory phaseFactory = new ChasseurKillPhase.Factory(victim);
                        ((PhaseCycle) program).insert(phaseFactory);
                    }
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
            return Extension.listBuilder()
                    .extend(DUSK_ACTIONS, b -> b.add(VoyanteDuskAction::new).id("Voyante"))
                    .extendSingle(MECHANIC_MODIFIERS,
                            new VoyanteSeesInspectedPlayersCard())
                    .build();
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
            return Extension.listBuilder()
                    .extend(PHASES, b ->
                            b.add(CupidonCouplePhase::new)
                                    .id("CupidonCouple")
                                    .orderPosition(-2))
                    .extendSingle(COMPOSITION_VALIDATORS,
                            new PossibleCouplesCupidonCompositionValidator())
                    .build();
        }

    }

    public static class CoupleRule extends Rule {
        public CoupleRule(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public List<Extension<?>> getExtensions() {
            return Extension.listBuilder()
                    .extend(WIN_CONDITIONS, b -> b
                            .add(new CoupleWinCondition())
                            .id("CoupleWin"))
                    .build();
        }

        @GameEvent
        private void onLGKill(LGKillEvent event) {
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
        public FundamentalsRule(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public List<Extension<?>> getExtensions() {
            return Extension.listBuilder()
                    .extend(PHASES, b -> {
                        b.add(DuskPhase::new).id("Dusk");
                        b.add(LoupsGarousVotePhase::new).id("LoupsGarouVote");
                        b.add(NextTimeOfDayPhase::new).id("NextTimeOfDay");
                        b.add(RevealAllKillsPhase::new).id("RevealAllKills");
                        b.add(VillageVotePhase::new).id("VillageVote");
                    })
                    .extend(CARDS, b -> {
                        b.add(LoupGarouCard.INSTANCE);
                        b.add(VillageoisCard.INSTANCE);
                    })
                    .extend(COMPOSITION_VALIDATORS, b -> {
                        b.add(new MultipleTeamsCompositionValidator());
                        b.add(new UniqueCardCompositionValidator());
                    })
                    .extend(SCOREBOARD_COMPONENTS, b -> {
                        b.add(new LobbyOwnerScoreboardComponent()).id("LobbyOwner");
                        b.add(new PlayersAliveScoreboardComponent()).id("PlayersAlive");
                        b.add(new CompositionScoreboardComponent()).id("Composition");
                        b.add(new CurrentVotesScoreboardComponent()).id("CurrentVotes");
                    })
                    .extend(INVENTORY_ITEMS, b -> {
                        b.add(new EditLobbyItem()).id("EditLobby");
                        b.add(new EditModsItem()).id("EditMods");
                        b.add(new QuitGameItem()).id("QuitGame");
                    })
                    .extend(WIN_CONDITIONS, b -> {
                        b.add(new EveryoneDeadWinCondition()).id("EveryoneDead");
                        b.add(new LoupsGarousWinCondition()).id("LoupsGarousWin");
                        b.add(new VillageWinCondition()).id("VillageWin");
                    })
                    .extend(CHAT_CHANNELS, b -> {
                        b.add(LGChatChannels.DAY);
                        b.add(LGChatChannels.DEAD);
                        b.add(LGChatChannels.LOUPS_GAROUS);
                    })
                    .build();
        }

    }
}

