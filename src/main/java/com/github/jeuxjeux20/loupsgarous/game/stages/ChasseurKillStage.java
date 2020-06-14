package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.ChasseurCard;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.ChasseurKillReason;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Killable;
import com.github.jeuxjeux20.loupsgarous.game.winconditions.PostponesWinConditions;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.concurrent.CompletableFuture;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.importantTip;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.info;

@PostponesWinConditions
public class ChasseurKillStage extends CountdownLGStage implements Killable {
    private final LGPlayer chasseur;
    private boolean killed;

    @Inject
    ChasseurKillStage(@Assisted LGGameOrchestrator orchestrator, @Assisted LGPlayer chasseur) {
        super(orchestrator);
        this.chasseur = chasseur;
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.builder(30)
                .finished(this::sendInfoMessage)
                .build();
    }

    @Override
    protected void start() {
        chasseur.getMinecraftPlayer().ifPresent(player -> {
            player.spigot().respawn();
            player.sendMessage(
                    importantTip("Vite ! Faites /lgkill <joueur> pour tirer votre balle juste avant de mourir !")
            );
            LGSoundStuff.ding(player);
        });
    }

    @Override
    public String getName() {
        return "Tir du chasseur";
    }

    @Override
    public String getTitle() {
        return "Le chasseur va tirer sa balle (ou non) !";
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    private void sendInfoMessage() {
        if (!killed) {
            orchestrator.chat().sendToEveryone(info("Le chasseur n'a pas tiré."));
        }
    }

    @Override
    public Check canPlayerKill(LGPlayer killer) {
        return Check
                .ensure(killer.getCard() instanceof ChasseurCard, "Vous n'êtes pas chasseur !")
                .and(killer == chasseur, "Pas maintenant !")
                .and(!killed, "Vous avez déjà tiré votre balle.");
    }

    @Override
    public void kill(LGPlayer killer, LGPlayer target) {
        canKill(killer, target).ifError(error -> {
            throw new IllegalArgumentException("Cannot kill player " + target.getName() + " : " + error);
        });
        killed = true;
        orchestrator.kills().instantly(target, ChasseurKillReason::new);
        getCountdown().interrupt();
    }

    public interface Factory {
        ChasseurKillStage create(LGGameOrchestrator orchestrator, LGPlayer chasseur);
    }
}
