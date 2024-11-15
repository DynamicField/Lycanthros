package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.Check;
import com.github.df.loupsgarous.Countdown;
import com.github.df.loupsgarous.LGSoundStuff;
import com.github.df.loupsgarous.chat.ComponentStyles;
import com.github.df.loupsgarous.chat.ComponentTemplates;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGGameTurnTime;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.PlayerPick;
import com.github.df.loupsgarous.interaction.LGInteractableKeys;
import com.github.df.loupsgarous.interaction.PickableConditions;
import com.github.df.loupsgarous.interaction.condition.PickConditions;
import com.github.df.loupsgarous.kill.LGKill;
import com.github.df.loupsgarous.kill.causes.NightKillCause;
import com.github.df.loupsgarous.powers.SorcierePower;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;
import me.lucko.helper.text.event.ClickEvent;
import me.lucko.helper.text.event.HoverEvent;
import me.lucko.helper.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import static com.github.df.loupsgarous.chat.LGChatStuff.*;
import static me.lucko.helper.text.format.TextColor.*;
import static me.lucko.helper.text.format.TextDecoration.BOLD;

@PhaseInfo(
        name = "Sorcière",
        title = "La sorcière va utiliser ses potions..."
)
public final class SorcierePotionPhase extends CountdownPhase {
    private final SorciereHeal heal;
    private final SorciereKill kill;

    public SorcierePotionPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        this.heal = new SorciereHeal(orchestrator);
        this.kill = new SorciereKill(orchestrator);
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(30);
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.getPlayers().stream().anyMatch(Check.predicate(SorcierePick::canSorciereAct)) &&
               orchestrator.getTurn().getTime() == LGGameTurnTime.NIGHT;
    }

    @Override
    protected void start() {
        heal.register(LGInteractableKeys.HEAL).bindWith(this);
        kill.register(LGInteractableKeys.KILL).bindWith(this);

        orchestrator.getPlayers().stream()
                .filter(Check.predicate(SorcierePick::canSorciereAct))
                .forEach(this::sendNotification);
    }

    public SorciereHeal heals() {
        return heal;
    }

    public SorciereKill kills() {
        return kill;
    }

    private void sendNotification(LGPlayer player) {
        player.minecraft(minecraftPlayer -> sendNotification(player, minecraftPlayer));
    }

    // This is really long.
    private void sendNotification(LGPlayer player, Player minecraftPlayer) {
        SorcierePower power = player.powers().getOrThrow(SorcierePower.class);

        TextComponent.Builder builder = TextComponent.builder("")
                .append(TextComponent.of("==== Vous êtes la sorcière !")
                        .color(TextColor.LIGHT_PURPLE)
                        .decoration(BOLD, true));

        if (power.isHealPotionAvailable()) {
            Set<LGKill> pendingKills = orchestrator.kills().pending().getAll();

            builder.append(TextComponent.of("\n" + HEAL_SYMBOL + " ").color(GREEN));

            if (pendingKills.isEmpty()) {
                builder.append(TextComponent.of("Personne ne va mourir !").color(WHITE).decoration(BOLD, true));
            } else {
                int i = 0;
                int endSeparator = pendingKills.size() - 2;

                for (Iterator<LGKill> iterator = pendingKills.iterator(); iterator.hasNext(); i++) {
                    LGKill kill = iterator.next();
                    String victimName = kill.getVictim().getName();

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
                        builder.append(TextComponent.of(i == endSeparator ? " et " : ", ", WHITE));
                    }
                }

                String finalText = pendingKills.size() > 1 ? " vont mourir cette nuit !" : " va mourir cette nuit !";
                builder.append(TextComponent.of(finalText, WHITE));
            }
        }

        if (power.isKillPotionAvailable()) {
            TextComponent poison = TextComponent.of("\n" + SKULL_SYMBOL + " Vous avez votre potion de poison !")
                    .color(RED);

            TextComponent.Builder tipBuilder = TextComponent.builder("\n").mergeStyle(ComponentStyles.TIP);

            tipBuilder.append(TextComponent.of("Faites "))
                    .append(ComponentTemplates.command("/lgkill", "<joueur>"))
                    .append(TextComponent.of(" pour l'utiliser et tuer quelqu'un !"));

            builder.append(poison).append(tipBuilder.build());
        }

        if (!power.isKillPotionAvailable() && !power.isHealPotionAvailable()) {
            builder.append(TextComponent.of("\nVous n'avez plus de potions.").color(YELLOW));
        }

        builder.append(TextComponent.of("\n====").color(LIGHT_PURPLE).decoration(BOLD, true));

        TextComponent message = builder.build();
        Text.sendMessage(minecraftPlayer, message);

        LGSoundStuff.ding(minecraftPlayer);
    }

    // PickData stuff

    private static abstract class SorcierePick extends PlayerPick {
        protected SorcierePick(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        protected final PickConditions<LGPlayer> pickConditions() {
            return conditionsBuilder()
                    .ensurePicker(SorcierePick::canSorciereAct)
                    .use(powerConditions())
                    .build();
        }

        protected abstract PickConditions<LGPlayer> powerConditions();

        protected SorcierePower getSorcierePower(LGPlayer player) {
            return player.powers().getOrThrow(SorcierePower.class);
        }

        public static Check canSorciereAct(LGPlayer player) {
            return Check.ensure(player.isAlive(), "Vous êtes mort !")
                    .and(player.powers().has(SorcierePower.class), "Vous n'êtes pas une sorcière !");
        }
    }

    public static class SorciereHeal extends SorcierePick {
        public SorciereHeal(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        protected PickConditions<LGPlayer> powerConditions() {
            return conditionsBuilder()
                    .ensurePicker(this::hasHealPotion, "Vous avez déjà utilisé votre potion de soin !")
                    .ensureTarget(LGPlayer::isGoingToDie, "Ce joueur ne va pas mourir ce tour ci.")
                    .build();
        }

        @Override
        protected void safePick(LGPlayer healer, LGPlayer target) {
            SorcierePower power = getSorcierePower(healer);

            power.useHealPotion();
            target.cancelFutureDeath();

            healer.sendMessage(
                    ChatColor.GREEN + " Glou glou, la potion guérit " + player(target.getName()) +
                    ChatColor.GREEN + " qui restera en vie cette nuit."
            );
        }

        private boolean hasHealPotion(LGPlayer player) {
            return getSorcierePower(player).isHealPotionAvailable();
        }
    }

    public static class SorciereKill extends SorcierePick {
        public SorciereKill(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        protected PickConditions<LGPlayer> powerConditions() {
            return conditionsBuilder()
                    .apply(PickableConditions::ensureKillTargetAlive)
                    .ensurePicker(this::hasKillPotion, "Vous avez déjà utilisé votre potion de mort !")
                    .ensure(this::isSorciereAwareAboutPlayerDeath, "Ce joueur va déjà mourir !")
                    .build();
        }

        @Override
        protected void safePick(LGPlayer killer, LGPlayer target) {
            SorcierePower power = getSorcierePower(killer);

            power.useKillPotion();
            target.dieLater(NightKillCause.INSTANCE);

            killer.sendMessage(
                    ChatColor.RED + " Glou glou, la potion empoisonne " + player(target.getName()) +
                    ChatColor.RED + " qui va mourir cette nuit."
            );
        }

        private boolean hasKillPotion(LGPlayer player) {
            return getSorcierePower(player).isKillPotionAvailable();
        }

        private boolean isSorciereAwareAboutPlayerDeath(LGPlayer picker, LGPlayer target) {
            return getSorcierePower(picker).isHealPotionAvailable() && target.isGoingToDie();
        }
    }
}
