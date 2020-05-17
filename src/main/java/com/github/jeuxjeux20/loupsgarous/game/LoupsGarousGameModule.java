package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.guicybukkit.command.CommandConfigurator;
import com.github.jeuxjeux20.loupsgarous.game.cards.*;
import com.github.jeuxjeux20.loupsgarous.game.chat.*;
import com.github.jeuxjeux20.loupsgarous.game.commands.*;
import com.github.jeuxjeux20.loupsgarous.game.composition.gui.CompositionGui;
import com.github.jeuxjeux20.loupsgarous.game.composition.validation.CompositionValidator;
import com.github.jeuxjeux20.loupsgarous.game.composition.validation.PossibleCouplesCupidonCompositionValidator;
import com.github.jeuxjeux20.loupsgarous.game.composition.validation.UniqueCardCompositionValidator;
import com.github.jeuxjeux20.loupsgarous.game.listeners.*;
import com.github.jeuxjeux20.loupsgarous.game.stages.*;
import com.github.jeuxjeux20.loupsgarous.game.stages.dusk.DuskStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.dusk.VoyanteDuskAction;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Types;
import org.bukkit.event.Listener;

import java.lang.reflect.ParameterizedType;

public final class LoupsGarousGameModule extends AbstractModule {
    @Override
    protected void configure() {
        configureBindings();

        configureListeners(Multibinder.newSetBinder(binder(), Listener.class));
        configureStages(Multibinder.newSetBinder(binder(), new TypeLiteral<AsyncLGGameStage.Factory<?>>() {
        }));
        configureCommands(Multibinder.newSetBinder(binder(), CommandConfigurator.class));
        configureChatChannels(Multibinder.newSetBinder(binder(), LGGameChatChannel.class));
        configureDuskActions(Multibinder.newSetBinder(binder(), DuskStage.Action.class));
        configureCards(Multibinder.newSetBinder(binder(), LGCard.class));
        configureCompositionValidators(Multibinder.newSetBinder(binder(), CompositionValidator.class));
    }

    private void configureBindings() {
        bind(LGGameManager.class).to(DefaultLGGameManager.class);

        install(new FactoryModuleBuilder()
                .implement(LGGameOrchestrator.class, MinecraftLGGameOrchestrator.class)
                .build(LGGameOrchestrator.Factory.class));

        install(new FactoryModuleBuilder()
                .implement(LGCardOrchestrator.class, MinecraftLGCardOrchestrator.class)
                .build(LGCardOrchestrator.Factory.class));

        install(new FactoryModuleBuilder().build(CompositionGui.Factory.class));

        bind(LGGameChatManager.class).to(DefaultLGGameChatManager.class);
        bind(AnonymizedNamesProvider.class).to(RandomAnonymizedNamesProvider.class);
        bind(LGScoreboardManager.class).to(DefaultLGScoreboardManager.class);
    }

    private void configureListeners(Multibinder<Listener> binder) {
        binder.addBinding().to(SwitchTimeOfDayListener.class);
        binder.addBinding().to(TellPlayerCardListener.class);
        binder.addBinding().to(TellWinnerListener.class);
        binder.addBinding().to(TellPlayersKilledListener.class);
        binder.addBinding().to(TellPlayerVoteListener.class);
        binder.addBinding().to(ChasseurKillOnDeathListener.class);
        binder.addBinding().to(PlayerJoinGameListener.class);
        binder.addBinding().to(PlayerDiesOnKillListener.class);
        binder.addBinding().to(CheckForEveryoneDeadListener.class);
        binder.addBinding().to(CheckForVillageWinListener.class);
        binder.addBinding().to(CheckForLoupsGarousWinListener.class);
        binder.addBinding().to(RedirectChatMessageListener.class);
        binder.addBinding().to(KillCoupleOnPartnerDeadListener.class);
        binder.addBinding().to(BossBarStageListener.class);
        binder.addBinding().to(PlayerJoinGameListener.class);
        binder.addBinding().to(DeadPlayerAsSpectatorListener.class);
        binder.addBinding().to(TellPlayerDevoteListener.class);
        binder.addBinding().to(UpdateScoreboardListener.class);
        binder.addBinding().to(ClearAllEffectsOnEndListener.class);
        binder.addBinding().to(TellStageTitleListener.class);
        binder.addBinding().to(TellVoteTipsListener.class);
    }

    private void configureStages(Multibinder<AsyncLGGameStage.Factory<?>> binder) {
        // Order for NextTimeOfDayStage doesn't really matter, as long as
        // stages check the time of day in shouldRun.
        addStageFactory(binder, CupidonCoupleStage.class);
        addStageFactory(binder, DuskStage.class);
        addStageFactory(binder, LoupGarouNightKillVoteStage.class);
        addStageFactory(binder, SorcierePotionStage.class);
        addStageFactory(binder, NextTimeOfDayStage.class);
        addStageFactory(binder, RevealAllKillsStage.class);
        addStageFactory(binder, VillageVoteStage.class);

        install(new FactoryModuleBuilder().build(ChasseurKillStage.Factory.class));
        // Do not add GameEndStage and GameStartStage!
    }

    private void configureCommands(Multibinder<CommandConfigurator> binder) {
        binder.addBinding().to(LGLookCommand.class);
        binder.addBinding().to(LGVoteCommand.class);
        binder.addBinding().to(LGKillCommand.class);
        binder.addBinding().to(LGPlayersCommand.class);
        binder.addBinding().to(LGDevoteCommand.class);
        binder.addBinding().to(LGCompositionCommand.class);
        binder.addBinding().to(LGKillCommand.class);
        binder.addBinding().to(LGHealCommand.class);
        binder.addBinding().to(LGCoupleCommand.class);
    }

    private void configureChatChannels(Multibinder<LGGameChatChannel> binder) {
        binder.addBinding().to(DayChatChannel.class);
        binder.addBinding().to(DeadChatChannel.class);
        binder.addBinding().to(LoupsGarousChatChannel.class);
        binder.addBinding().to(OutOfGameChatChannel.class);
        binder.addBinding().to(LoupsGarousVoteChatChannel.class);
    }

    private void configureDuskActions(Multibinder<DuskStage.Action> binder) {
        binder.addBinding().to(VoyanteDuskAction.class);
    }

    private void configureCards(Multibinder<LGCard> binder) {
        binder.addBinding().to(ChasseurCard.class);
        binder.addBinding().to(CupidonCard.class);
        binder.addBinding().to(LoupGarouCard.class);
        binder.addBinding().to(PetiteFilleCard.class);
        binder.addBinding().to(SorciereCard.class);
        binder.addBinding().to(VillageoisCard.class);
        binder.addBinding().to(VoyanteCard.class);
    }

    private void configureCompositionValidators(Multibinder<CompositionValidator> binder) {
        binder.addBinding().to(PossibleCouplesCupidonCompositionValidator.class);
        binder.addBinding().to(UniqueCardCompositionValidator.class);
    }

    private <T extends AsyncLGGameStage>
    void addStageFactory(Multibinder<AsyncLGGameStage.Factory<?>> binder, Class<T> clazz) {
        // Basically, it's AsyncLGGameStage.Factory<T> with T being the given class.
        ParameterizedType factoryType
                = Types.newParameterizedTypeWithOwner(AsyncLGGameStage.class, AsyncLGGameStage.Factory.class, clazz);

        @SuppressWarnings("unchecked")
        TypeLiteral<AsyncLGGameStage.Factory<T>> factoryLiteral
                = (TypeLiteral<AsyncLGGameStage.Factory<T>>) TypeLiteral.get(factoryType);

        install(new FactoryModuleBuilder()
                .build(factoryLiteral));

        binder.addBinding().to(factoryLiteral);
    }
}
