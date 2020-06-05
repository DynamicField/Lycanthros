package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.ComponentStyles;
import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.cards.SorciereCard;
import com.github.jeuxjeux20.loupsgarous.game.killreasons.NightKillReason;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Healable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Killable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PickableProvider;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;
import me.lucko.helper.text.event.ClickEvent;
import me.lucko.helper.text.event.HoverEvent;
import me.lucko.helper.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;
import static me.lucko.helper.text.format.TextColor.*;
import static me.lucko.helper.text.format.TextDecoration.BOLD;
import static me.lucko.helper.text.format.TextDecoration.ITALIC;

public class SorcierePotionStage extends AsyncLGGameStage implements CountdownTimedStage {
    private final Countdown countdown;
    private final Healable healable;
    private final Killable killable;

    @Inject
    SorcierePotionStage(@Assisted LGGameOrchestrator orchestrator) {
        super(orchestrator);
        healable = new SorciereHealable();
        killable = new SorciereKillable();

        countdown = Countdown.builder(30).apply(this::addTickEvents).build(orchestrator);
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

        TextComponent.Builder builder = TextComponent.builder("")
                .append(TextComponent.of("==== Vous êtes la sorcière !")
                        .color(TextColor.LIGHT_PURPLE)
                        .decoration(BOLD, true));

        if (card.hasHealPotion()) {
            Set<LGKill> pendingKills = orchestrator.getPendingKills();

            builder.append(TextComponent.of("\n" + HEAL_SYMBOL + " ").color(GREEN));

            if (pendingKills.isEmpty()) {
                builder.append(TextComponent.of("Personne ne va mourir !").color(WHITE).decoration(BOLD, true));
            } else {
                int i = 0;
                int endSeparator = pendingKills.size() - 2;

                for (Iterator<LGKill> iterator = pendingKills.iterator(); iterator.hasNext(); i++) {
                    LGKill kill = iterator.next();
                    String victimName = kill.getWhoDied().getName();

                    builder.append(TextComponent.of(victimName + " ", RED, Collections.singleton(BOLD)));

                    String command = "/lgheal " + victimName;

                    TextComponent hoverHeal = TextComponent.of("Cliquez ici pour soigner " + victimName + " !");

                    TextComponent healButton = TextComponent.of("[Soigner]")
                            .mergeStyle(ComponentStyles.CLICKABLE)
                            .color(GREEN)
                            .hoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverHeal))
                            .clickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

                    builder.append(healButton);

                    if (iterator.hasNext()) {
                        builder.append(TextComponent.of(i == endSeparator ? " et " : ",", WHITE));
                    }
                }

                String commandUsageTip;

                if (pendingKills.size() > 1) {
                    builder.append(TextComponent.of(" vont mourir cette nuit."));
                    commandUsageTip = "Faites /lgheal <joueur> pour soigner un de ces joueurs";
                } else {
                    builder.append(TextComponent.of(" va mourir cette nuit."));
                    commandUsageTip = "Faites /lgheal " + pendingKills.iterator().next().getWhoDied().getName() +
                                      " pour soigner ce joueur.";
                }

                builder.append(TextComponent.of("\n" + commandUsageTip).color(GRAY).decoration(ITALIC, true));
            }
        }

        if (card.hasKillPotion()) {
            TextComponent poison = TextComponent.of("\n" + SKULL_SYMBOL + " Vous avez votre potion de poison !")
                    .color(RED);

            TextComponent tip = TextComponent.of("\nFaites /lgkill <joueur> pour l'utiliser et tuer quelqu'un !")
                    .color(GRAY)
                    .decoration(ITALIC, true);

            builder.append(poison).append(tip);
        }

        if (!card.hasKillPotion() && !card.hasHealPotion()) {
            builder.append(TextComponent.of("\nVous n'avez plus de potions.").color(YELLOW));
        }

        builder.append(TextComponent.of("\n====").color(LIGHT_PURPLE).decoration(BOLD, true));


        TextComponent message = builder.build();
        Text.sendMessage(minecraftPlayer, message);

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
