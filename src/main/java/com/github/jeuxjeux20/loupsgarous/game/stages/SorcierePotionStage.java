package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.cards.SorciereCard;
import com.github.jeuxjeux20.loupsgarous.game.killreasons.NightKillReason;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Healable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Killable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PickableProvider;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.github.jeuxjeux20.loupsgarous.util.WordingUtils;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;

public class SorcierePotionStage extends AsyncLGGameStage implements CountdownTimedStage {
    private final TickEventCountdown countdown;
    private final Healable healable;
    private final Killable killable;

    @Inject
    SorcierePotionStage(@Assisted LGGameOrchestrator orchestrator) {
        super(orchestrator);
        healable = new SorciereHealable();
        killable = new SorciereKillable();

        countdown = new TickEventCountdown(this, 30);
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.getGame().getAlivePlayers().anyMatch(Check.predicate(this::canAct))
               && orchestrator.getGame().getTurn().getTime() == LGGameTurnTime.NIGHT;
    }

    @Override
    public CompletableFuture<Void> run() {
        orchestrator.getGame().getAlivePlayers()
                .filter(Check.predicate(this::canAct))
                .forEach(this::sendNotification);

        return countdown.start();
    }

    @Override
    public @NotNull String getName() {
        return "Sorcière";
    }

    @Override
    public Optional<String> getTitle() {
        return Optional.of("La sorcière va utiliser ses potions...");
    }

    @Override
    public Countdown getCountdown() {
        return countdown;
    }

    @Override
    public Iterable<? extends PickableProvider> getAllComponents() {
        return Arrays.asList(healable, killable);
    }

    private Check canAct(LGPlayer player) {
        return Check.ensure(player.isAlive(), "Vous êtes mort !")
                .and(player.getCard() instanceof SorciereCard, "Vous n'êtes pas une sorcière !");
    }

    private void sendNotification(LGPlayer player) {
        player.getMinecraftPlayer().ifPresent(minecraftPlayer -> sendNotification(player, minecraftPlayer));
    }

    private void sendNotification(LGPlayer player, Player minecraftPlayer) {
        SorciereCard card = ((SorciereCard) player.getCard()); // The checks ensure that it is a SorciereCard.

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(IMPORTANT_TIP_COLOR)
                .append("==== Vous êtes la sorcière !");

        if (card.hasHealPotion()) {
            List<LGKill> pendingKills = orchestrator.getPendingKills();

            messageBuilder.append('\n')
                    .append(ChatColor.GREEN)
                    .append(HEAL_SYMBOL)
                    .append(ChatColor.WHITE)
                    .append(" ");

            if (pendingKills.isEmpty()) {
                messageBuilder.append(ChatColor.BOLD)
                        .append("Personne ne va mourir !");
            } else {
                String killedPlayersNames = pendingKills.stream()
                        .map(x -> ChatColor.RED + ChatColor.BOLD.toString() + x.getWhoDied().getName())
                        .collect(Collectors.collectingAndThen(Collectors.toList(),
                                WordingUtils.joiningLastDelimiter(ChatColor.WHITE + ", ", ChatColor.WHITE + " et ")));

                messageBuilder.append(killedPlayersNames);

                String commandUsageTip;

                if (pendingKills.size() > 1) {
                    messageBuilder.append(" vont mourir cette nuit.");
                    commandUsageTip = "Faites /lgheal <joueur> pour soigner un de ces joueurs";
                } else {
                    messageBuilder.append(" va mourir cette nuit.");
                    commandUsageTip = "Faites /lgheal " + pendingKills.get(0).getWhoDied().getName() +
                                      " pour soigner ce joueur.";
                }


                messageBuilder.append('\n')
                        .append(TIP_COLOR)
                        .append(commandUsageTip);
            }
        }

        if (card.hasKillPotion()) {
            messageBuilder.append('\n')
                    .append(ChatColor.RED)
                    .append(SKULL_SYMBOL)
                    .append(" Vous avez votre potion de poison !")
                    .append('\n')
                    .append(TIP_COLOR)
                    .append("Faites /lgkill <joueur> pour l'utiliser et tuer quelqu'un !");
        }

        if (!card.hasKillPotion() && !card.hasHealPotion()) {
            messageBuilder.append('\n')
                    .append(ChatColor.YELLOW)
                    .append("Vous n'avez plus de potions.");
        }

        messageBuilder.append('\n')
                .append(IMPORTANT_TIP_COLOR)
                .append("====");

        minecraftPlayer.sendMessage(messageBuilder.toString());

        LGSoundStuff.ding(minecraftPlayer);
    }

    public class SorciereHealable implements Healable {
        private SorciereHealable() {
        }

        @Override
        public Check canPlayerHeal(LGPlayer healer) {
            return canAct(healer)
                    .and(() -> ((SorciereCard) healer.getCard()).hasHealPotion(),
                            "Vous avez déjà utilisé votre potion de soin !");
        }

        @Override
        public Check canHeal(LGPlayer healer, LGPlayer target) {
            return canPlayerHeal(healer)
                    .and(() -> orchestrator.getPendingKills().stream().anyMatch(x -> x.getWhoDied() == target),
                            "Ce joueur ne va pas mourir ce tour ci.");
        }

        @Override
        public void heal(LGPlayer healer, LGPlayer target) {
            canHeal(healer, target).ifError(error -> {
                throw new IllegalArgumentException("Cannot heal player " + target.getName() + ": " + error);
            });

            SorciereCard card = (SorciereCard) healer.getCard();

            card.useHealPotion();
            orchestrator.getPendingKills().removeIf(x -> x.getWhoDied() == target);

            healer.getMinecraftPlayer().ifPresent(player ->
                    player.sendMessage(
                            ChatColor.GREEN + " Glou glou, la potion guérit " + player(target.getName()) +
                            ChatColor.GREEN + " qui restera en vie cette nuit."
                    )
            );
        }
    }

    public class SorciereKillable implements Killable {
        private SorciereKillable() {
        }

        @Override
        public Check canPlayerKill(LGPlayer killer) {
            return canAct(killer)
                    .and(() -> ((SorciereCard) killer.getCard()).hasKillPotion(),
                            "Vous avez déjà utilisé votre potion de mort !");
        }

        @Override
        public void kill(LGPlayer killer, LGPlayer target) {
            canKill(killer, target).ifError(error -> {
                throw new IllegalArgumentException("Cannot kill player " + target.getName() + ": " + error);
            });

            SorciereCard card = (SorciereCard) killer.getCard();

            card.useKillPotion();
            orchestrator.getPendingKills().add(LGKill.of(target, NightKillReason::new));

            killer.getMinecraftPlayer().ifPresent(player ->
                    player.sendMessage(
                            ChatColor.RED + " Glou glou, la potion empoisonne " + player(target.getName()) +
                            ChatColor.RED + " qui va mourir cette nuit."
                    )
            );
        }
    }
}
