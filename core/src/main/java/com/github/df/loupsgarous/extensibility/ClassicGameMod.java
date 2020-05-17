package com.github.df.loupsgarous.extensibility;

import com.github.df.loupsgarous.cards.*;
import com.github.df.loupsgarous.phases.*;
import com.github.df.loupsgarous.cards.*;
import com.github.df.loupsgarous.cards.composition.validation.MultipleTeamsCompositionValidator;
import com.github.df.loupsgarous.cards.composition.validation.PossibleCouplesCupidonCompositionValidator;
import com.github.df.loupsgarous.cards.composition.validation.UniqueCardCompositionValidator;
import com.github.df.loupsgarous.chat.LGChatChannels;
import com.github.df.loupsgarous.chat.PetiteFilleSpiesOnLoupsGarous;
import com.github.df.loupsgarous.event.GameEvent;
import com.github.df.loupsgarous.event.LGKillEvent;
import com.github.df.loupsgarous.extensibility.registry.GameRegistries;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.vote.outcome.MaireVoteOutcomeTransformer;
import com.github.df.loupsgarous.inventory.EditLobbyItem;
import com.github.df.loupsgarous.inventory.EditModsItem;
import com.github.df.loupsgarous.inventory.QuitGameItem;
import com.github.df.loupsgarous.kill.LGKill;
import com.github.df.loupsgarous.phases.*;
import com.github.df.loupsgarous.phases.dusk.DuskPhase;
import com.github.df.loupsgarous.phases.dusk.VoyanteDuskAction;
import com.github.df.loupsgarous.powers.ChasseurPower;
import com.github.df.loupsgarous.scoreboard.CompositionScoreboardComponent;
import com.github.df.loupsgarous.scoreboard.CurrentVotesScoreboardComponent;
import com.github.df.loupsgarous.scoreboard.LobbyOwnerScoreboardComponent;
import com.github.df.loupsgarous.scoreboard.PlayersAliveScoreboardComponent;
import com.github.df.loupsgarous.winconditions.CoupleWinCondition;
import com.github.df.loupsgarous.winconditions.EveryoneDeadWinCondition;
import com.github.df.loupsgarous.winconditions.LoupsGarousWinCondition;
import com.github.df.loupsgarous.winconditions.VillageWinCondition;
import org.bukkit.event.EventPriority;

@ModInfo(
        name = "Jeu classique",
        enabledByDefault = true,
        hidden = true
)
public class ClassicGameMod extends Mod {
    public ClassicGameMod(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected void activate() {
        orchestrator.getGameRegistry(GameRegistries.CARDS)
                .registerMany(r -> {
                    r.register(LoupGarouCard.INSTANCE);
                    r.register(VillageoisCard.INSTANCE);
                    r.register(CupidonCard.INSTANCE);
                    r.register(SorciereCard.INSTANCE);
                    r.register(PetiteFilleCard.INSTANCE);
                    r.register(VoyanteCard.INSTANCE);
                    r.register(ChasseurCard.INSTANCE);
                })
                .bindWith(this);

        orchestrator.getGameRegistry(GameRegistries.PHASES)
                .registerMany(r -> {
                    r.register(CupidonCouplePhase::new).name("CupidonCouple");
                    r.register(DuskPhase::new).name("Dusk");
                    r.register(LoupsGarousVotePhase::new).name("LoupsGarouVote");
                    r.register(NextTimeOfDayPhase::new).name("NextTimeOfDay");
                    r.register(RevealAllKillsPhase::new).name("RevealAllKills");
                    r.register(MaireElectionPhase::new).name("MaireElection");
                    r.register(VillageVotePhase::new).name("VillageVote");
                })
                .bindWith(this);

        orchestrator.getGameRegistry(GameRegistries.DUSK_ACTIONS)
                .registerMany(r -> r.register(VoyanteDuskAction::new).name("Voyante"))
                .bindWith(this);

        orchestrator.getGameRegistry(GameRegistries.COMPOSITION_VALIDATORS)
                .registerMany(r -> {
                    r.register(new MultipleTeamsCompositionValidator());
                    r.register(new UniqueCardCompositionValidator());
                    r.register(new PossibleCouplesCupidonCompositionValidator());
                })
                .bindWith(this);

        orchestrator.getGameRegistry(GameRegistries.SCOREBOARD_COMPONENTS)
                .registerMany(r -> {
                    r.register(new LobbyOwnerScoreboardComponent()).name("LobbyOwner");
                    r.register(new PlayersAliveScoreboardComponent()).name("PlayersAlive");
                    r.register(new CompositionScoreboardComponent()).name("Composition");
                    r.register(new CurrentVotesScoreboardComponent()).name("CurrentVotes");
                })
                .bindWith(this);

        orchestrator.getGameRegistry(GameRegistries.INVENTORY_ITEMS)
                .registerMany(r -> {
                    r.register(new EditLobbyItem(orchestrator)).name("EditLobby");
                    r.register(new EditModsItem(orchestrator)).name("EditMods");
                    r.register(new QuitGameItem(orchestrator)).name("QuitGame");
                })
                .bindWith(this);

        orchestrator.getGameRegistry(GameRegistries.WIN_CONDITIONS)
                .registerMany(r -> {
                    r.register(new EveryoneDeadWinCondition()).name("EveryoneDead");
                    r.register(new LoupsGarousWinCondition()).name("LoupsGarousWin");
                    r.register(new VillageWinCondition()).name("VillageWin");
                    r.register(new CoupleWinCondition()).name("CoupleWin");
                })
                .bindWith(this);

        orchestrator.getGameRegistry(GameRegistries.MECHANIC_MODIFIERS)
                .registerMany(r -> {
                    r.register(new VoyanteSeesInspectedPlayersCard());
                    r.register(new PetiteFilleSpiesOnLoupsGarous());
                })
                .bindWith(this);

        orchestrator.getGameRegistry(GameRegistries.PLAYER_VOTE_OUTCOME_TRANSFORMERS)
                .registerMany(r -> r.register(new MaireVoteOutcomeTransformer()))
                .bindWith(this);

        orchestrator.getGameRegistry(GameRegistries.CHAT_CHANNELS)
                .registerMany(r -> {
                    r.register(LGChatChannels.DAY);
                    r.register(LGChatChannels.DEAD);
                    r.register(LGChatChannels.LOUPS_GAROUS);
                })
                .bindWith(this);
    }

    @GameEvent(priority = EventPriority.HIGH)
    public void onLGKillChasseur(LGKillEvent event) {
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