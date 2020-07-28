package com.github.jeuxjeux20.loupsgarous.stages;

import com.github.jeuxjeux20.loupsgarous.ComponentStyles;
import com.github.jeuxjeux20.loupsgarous.ComponentTemplates;
import com.github.jeuxjeux20.loupsgarous.Countdown;
import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.AbstractPlayerPick;
import com.github.jeuxjeux20.loupsgarous.interaction.InteractableRegisterer;
import com.github.jeuxjeux20.loupsgarous.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.kill.causes.ChasseurKillCause;
import com.github.jeuxjeux20.loupsgarous.powers.ChasseurPower;
import com.github.jeuxjeux20.loupsgarous.winconditions.PostponesWinConditions;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.info;

@PostponesWinConditions
@StageInfo(
        name = "Tir du chasseur",
        isTemporary = true
)
public final class ChasseurKillStage extends CountdownLGStage {
    private final LGPlayer chasseur;

    private final ChasseurKill killable;

    @Inject
    ChasseurKillStage(@Assisted LGGameOrchestrator orchestrator, @Assisted LGPlayer chasseur) {
        super(orchestrator);
        this.chasseur = chasseur;
        this.killable = InteractableRegisterer.of(ChasseurKill::new)
                .as(LGInteractableKeys.KILL)
                .boundWith(this);

        orchestrator.stages().descriptors().get(getClass())
                .setTitle("Le chasseur " + chasseur.getName() + " va tirer sa balle (ou non) !");

        // Reset the title after the stage ends.
        bind(() -> this.orchestrator.stages().descriptors().invalidate(getClass()));
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
        if (!killable.killed) {
            orchestrator.chat().sendToEveryone(info("Le chasseur n'a pas tiré."));
        }
    }

    public ChasseurKill kills() {
        return killable;
    }

    public interface Factory {
        ChasseurKillStage create(LGGameOrchestrator orchestrator, LGPlayer chasseur);
    }

    public final class ChasseurKill extends AbstractPlayerPick {
        boolean killed = false;

        private ChasseurKill() {
            super(ChasseurKillStage.super.orchestrator);
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

        @Override
        protected void safePick(LGPlayer picker, LGPlayer target) {
            killed = true;
            target.die(ChasseurKillCause.INSTANCE);
            getCountdown().interrupt();
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
}
