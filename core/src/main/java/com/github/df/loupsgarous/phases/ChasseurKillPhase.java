package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.Countdown;
import com.github.df.loupsgarous.LGSoundStuff;
import com.github.df.loupsgarous.chat.ComponentStyles;
import com.github.df.loupsgarous.chat.ComponentTemplates;
import com.github.df.loupsgarous.extensibility.ContentFactory;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.PlayerPick;
import com.github.df.loupsgarous.interaction.LGInteractableKeys;
import com.github.df.loupsgarous.interaction.condition.PickConditions;
import com.github.df.loupsgarous.kill.causes.ChasseurKillCause;
import com.github.df.loupsgarous.powers.ChasseurPower;
import com.github.df.loupsgarous.winconditions.PostponesWinConditions;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;

import static com.github.df.loupsgarous.chat.LGChatStuff.info;

@PostponesWinConditions
@PhaseInfo(
        name = "Tir du chasseur",
        isTemporary = true
)
public final class ChasseurKillPhase extends CountdownPhase {
    private final LGPlayer chasseur;
    private final ChasseurKill killable;

    public ChasseurKillPhase(LGGameOrchestrator orchestrator, LGPlayer chasseur) {
        super(orchestrator);

        this.chasseur = chasseur;
        this.killable = new ChasseurKill(orchestrator, chasseur);

        getDescriptor()
                .setTitle("Le chasseur " + chasseur.getName() + " va tirer sa balle (ou non) !");
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(30);
    }

    @Override
    public boolean shouldRun() {
        return killable.canSomeonePick();
    }

    @Override
    protected void start() {
        killable.register(LGInteractableKeys.KILL).bindWith(this);

        chasseur.minecraft(player -> {
            player.spigot().respawn();

            TextComponent message = TextComponent.of("Vite ! Faites ").mergeStyle(ComponentStyles.IMPORTANT_TIP)
                    .append(ComponentTemplates.command("/lgkill", "<joueur>"))
                    .append(TextComponent.of(" pour tirer votre balle juste avant de mourir !"));

            Text.sendMessage(player, message);
            LGSoundStuff.ding(player);
        });
    }

    @Override
    protected void finish() {
        if (!killable.hasChasseurKilledSomeone()) {
            orchestrator.chat().sendToEveryone(info("Le chasseur n'a pas tiré."));
        }
    }

    public ChasseurKill kills() {
        return killable;
    }

    public LGPlayer getChasseur() {
        return chasseur;
    }

    public static class ChasseurKill extends PlayerPick {
        private boolean killed = false;
        private final LGPlayer chasseur;

        public ChasseurKill(LGGameOrchestrator orchestrator, LGPlayer chasseur) {
            super(orchestrator);
            this.chasseur = chasseur;
        }

        @Override
        public PickConditions<LGPlayer> pickConditions() {
            return conditionsBuilder()
                    .ensurePicker(this::isChasseur, "Vous n'êtes pas chasseur !")
                    .ensurePicker(this::isTheirTurn, "Ce n'est pas à votre tour !")
                    .ensurePicker(this::canShoot, "Vous avez déjà tiré votre balle.")
                    .ensureTarget(LGPlayer::isAlive, "La cible est déjà morte.")
                    .build();
        }

        public LGPlayer getChasseur() {
            return chasseur;
        }

        public boolean hasChasseurKilledSomeone() {
            return killed;
        }

        @Override
        protected void safePick(LGPlayer picker, LGPlayer target) {
            killed = true;
            target.die(ChasseurKillCause.INSTANCE);
            orchestrator.phases().current().stop();
        }

        private boolean isChasseur(LGPlayer picker) {
            return picker.powers().has(ChasseurPower.class);
        }

        private boolean isTheirTurn(LGPlayer picker) {
            return picker == chasseur;
        }

        private boolean canShoot(LGPlayer picker) {
            return !killed;
        }
    }

    public static class Factory implements ContentFactory<ChasseurKillPhase> {
        private final LGPlayer chasseur;

        public Factory(LGPlayer chasseur) {
            this.chasseur = chasseur;
        }

        @Override
        public ChasseurKillPhase create(LGGameOrchestrator gameOrchestrator) {
            return new ChasseurKillPhase(gameOrchestrator, chasseur);
        }
    }
}
