package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.ChasseurCard;
import com.github.jeuxjeux20.loupsgarous.game.killreasons.ChasseurKillReason;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Killable;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.importantTip;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.info;

public class ChasseurKillStage extends AsyncLGGameStage implements CountdownTimedStage, Killable {
    private final LGPlayer chasseur;
    private final TickEventCountdown countdown;
    private boolean killed;

    @Inject
    ChasseurKillStage(@Assisted LGGameOrchestrator orchestrator, @Assisted LGPlayer chasseur) {
        super(orchestrator);
        this.chasseur = chasseur;

        countdown = createCountdown();
    }

    @Override
    public CompletableFuture<Void> run() {
        chasseur.getMinecraftPlayer().ifPresent(player -> {
            player.spigot().respawn();
            player.sendMessage(
                    importantTip("Vite ! Faites /lgkill <joueur> pour tirer votre balle juste avant de mourir !")
            );
            LGSoundStuff.ding(player);
        });

        return countdown.start().thenRun(this::sendInfoMessage);
    }

    @NotNull
    private TickEventCountdown createCountdown() {
        return new TickEventCountdown(this, 30);
    }

    private void sendInfoMessage() {
        if (!killed) {
            orchestrator.sendToEveryone(info("Le chasseur n'a pas tiré."));
        }
    }

    @Override
    public @Nullable String getName() {
        return "Tir du chasseur";
    }

    @Override
    public Optional<String> getTitle() {
        return Optional.of("Le chasseur va tirer sa balle (ou non) !");
    }

    @Override
    public Countdown getCountdown() {
        return countdown;
    }

    @Override
    public boolean isTemporary() {
        return true;
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
        orchestrator.killInstantly(target, ChasseurKillReason::new);
        countdown.interrupt();
    }

    public interface Factory {
        ChasseurKillStage create(LGGameOrchestrator orchestrator, LGPlayer chasseur);
    }
}
