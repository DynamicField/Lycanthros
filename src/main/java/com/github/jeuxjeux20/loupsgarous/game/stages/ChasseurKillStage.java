package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.ComponentStyles;
import com.github.jeuxjeux20.loupsgarous.ComponentTemplates;
import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.ChasseurCard;
import com.github.jeuxjeux20.loupsgarous.game.interaction.AbstractPlayerPickable;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.game.kill.causes.ChasseurKillReason;
import com.github.jeuxjeux20.loupsgarous.game.winconditions.PostponesWinConditions;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.info;

@PostponesWinConditions
@StageInfo(
        name = "Tir du chasseur",
        title = "Le chasseur va tirer sa balle (ou non) !", // TODO: Add player's name
        isTemporary = true
)
public class ChasseurKillStage extends CountdownLGStage {
    private final LGPlayer chasseur;

    private final ChasseurKillable killable;

    @Inject
    ChasseurKillStage(@Assisted LGGameOrchestrator orchestrator, @Assisted LGPlayer chasseur) {
        super(orchestrator);
        this.chasseur = chasseur;
        this.killable = new ChasseurKillable();

        registerInteractable(LGInteractableKeys.KILL, killable);
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
        chasseur.getMinecraftPlayer().ifPresent(player -> {
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

    public ChasseurKillable kills() {
        return killable;
    }

    public interface Factory {
        ChasseurKillStage create(LGGameOrchestrator orchestrator, LGPlayer chasseur);
    }

    public final class ChasseurKillable extends AbstractPlayerPickable {
        boolean killed = false;

        private ChasseurKillable() {
            super(ChasseurKillStage.super.orchestrator);
        }

        @Override
        public PickConditions<LGPlayer> pickConditions() {
            return FunctionalPickConditions.<LGPlayer>builder()
                    .ensurePicker(this::isChasseur, "Vous n'êtes pas chasseur !")
                    .ensurePicker(this::isTheirTurn, "Ce n'est pas à votre tour !")
                    .ensurePicker(this::canShoot, "Vous avez déjà tiré votre balle.")
                    .ensureTarget(LGPlayer::isAlive, "La cible est déjà morte.")
                    .build();
        }

        @Override
        protected void safePick(LGPlayer picker, LGPlayer target) {
            conditions().throwIfInvalid(picker, target);

            killed = true;
            orchestrator.kills().instantly(target, ChasseurKillReason.INSTANCE);
            getCountdown().interrupt();
        }

        private boolean isChasseur(LGPlayer picker) {
            return picker.getCard() instanceof ChasseurCard;
        }

        private boolean isTheirTurn(LGPlayer picker) {
            return picker == chasseur;
        }

        private boolean canShoot(LGPlayer picker) {
            return !killed;
        }
    }
}
