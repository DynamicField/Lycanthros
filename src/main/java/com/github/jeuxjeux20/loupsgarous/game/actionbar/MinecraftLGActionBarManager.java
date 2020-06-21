package com.github.jeuxjeux20.loupsgarous.game.actionbar;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.PlayerGameOutcome;
import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.CompositionValidator.Problem;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.event.CountdownTickEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageStartedEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.StageEventUtils;
import com.github.jeuxjeux20.loupsgarous.game.stages.TimedStage;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import me.lucko.helper.time.DurationFormatter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class MinecraftLGActionBarManager implements LGActionBarManager {
    private final LGGameOrchestrator orchestrator;

    @Inject
    MinecraftLGActionBarManager(@Assisted LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public void update() {
        for (LGPlayer player : orchestrator.game().getPlayers()) {
            update(player);
        }
    }

    @Override
    public TerminableModule createUpdateModule() {
        return new UpdateModule();
    }

    private void update(LGPlayer player) {
        player.getMinecraftPlayer().ifPresent(minecraftPlayer -> {
            List<BaseComponent> components = new ArrayList<>();

            renderParts(components, player,
                    this::createStateComponents,
                    this::createTimeComponents);

            BaseComponent[] componentsArray = components.toArray(new BaseComponent[0]);

            minecraftPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, componentsArray);
        });
    }

    private List<BaseComponent> createStateComponents(LGPlayer player) {
        List<BaseComponent> components = new ArrayList<>();

        if (orchestrator.state() == LGGameState.WAITING_FOR_PLAYERS) {
            components.add(new TextComponent("En attente"));

            addCompositionProblemComponent(components);
        } else if (orchestrator.state() == LGGameState.READY_TO_START) {
            components.add(new TextComponent("Départ dans "));

            Optional<TimedStage> maybeTimedStage = orchestrator.stages().current().getComponent(TimedStage.class);

            TextComponent numberComponent = maybeTimedStage.map(this::numberComponent)
                    .orElseGet(() -> new TextComponent("?"));
            numberComponent.setColor(ChatColor.AQUA);
            numberComponent.setBold(true);

            components.add(numberComponent);

            components.add(new TextComponent(" secondes"));

            addCompositionProblemComponent(components);
        } else if (orchestrator.state() == LGGameState.STARTED) {
            components.add(new TextComponent("Vous êtes : "));

            LGCard card = player.getCard();

            TextComponent cardComponent = new TextComponent(card.getName());
            cardComponent.setColor(card.getColor().asBungee());
            cardComponent.setBold(true);

            components.add(cardComponent);
        } else if (orchestrator.state() == LGGameState.FINISHED) {
            LGEnding ending = orchestrator.game().getEnding()
                    .orElseThrow(() -> new AssertionError("How is the ending null? The game's finished."));

            switch (ending.getOutcomeFor(player)) {
                case WIN:
                    components.add(new TextComponent("Vous avez gagné !"));
                    break;
                case LOSE:
                    components.add(new TextComponent("Vous avez perdu !"));
                    break;
                case SHRUG:
                    components.add(new TextComponent("Vous avez... " + PlayerGameOutcome.SHRUG_EMOJI));
                    break;
            }
        }

        return components;
    }

    private void addCompositionProblemComponent(List<BaseComponent> components) {
        if (orchestrator.lobby().getWorstCompositionProblemType() == Problem.Type.IMPOSSIBLE) {
            TextComponent component = new TextComponent(" (Composition invalide !)");
            component.setColor(ChatColor.RED);
            components.add(component);
        } else if (orchestrator.lobby().getWorstCompositionProblemType() == Problem.Type.RULE_BREAKING) {
            TextComponent component = new TextComponent(" (Composition contre les règles)");
            component.setColor(ChatColor.YELLOW);
            components.add(component);
        }
    }

    private List<BaseComponent> createTimeComponents(LGPlayer player) {
        List<BaseComponent> components = new ArrayList<>();

        if (!orchestrator.lobby().isLocked()) {
            TextComponent slotsComponent = new TextComponent(orchestrator.lobby().getSlotsDisplay());
            slotsComponent.setBold(true);
            slotsComponent.setColor(ChatColor.GREEN);

            components.add(slotsComponent);
        } else if (orchestrator.state() == LGGameState.STARTED) {
            orchestrator.stages().current().getComponent(TimedStage.class).ifPresent(timedStage -> {
                Duration secondsLeftDuration = Duration.ofSeconds(timedStage.getSecondsLeft());
                String formattedDuration = DurationFormatter.CONCISE.format(secondsLeftDuration);

                TextComponent timeComponent = new TextComponent(formattedDuration);
                timeComponent.setBold(true);
                timeComponent.setColor(ChatColor.GREEN);

                components.add(timeComponent);
            });
        }

        return components;
    }

    private void addHyphen(List<BaseComponent> components) {
        components.add(new TextComponent(" - "));
    }

    private void renderParts(List<BaseComponent> components, LGPlayer player, Part... parts) {
        for (int i = 0; i < parts.length; i++) {
            Part part = parts[i];

            List<BaseComponent> partComponents = part.render(player);

            if (i != 0 && !partComponents.isEmpty()) {
                addHyphen(components);
            }

            components.addAll(partComponents);
        }
    }

    private TextComponent numberComponent(TimedStage timedStage) {
        return new TextComponent(String.valueOf(timedStage.getSecondsLeft()));
    }

    @FunctionalInterface
    private interface Part {
        List<BaseComponent> render(LGPlayer player);
    }

    private final class UpdateModule implements TerminableModule {
        @Override
        public void setup(@Nonnull TerminableConsumer consumer) {
            Events.subscribe(CountdownTickEvent.class)
                    .filter(e -> StageEventUtils.isCurrentStageCountdownEvent(orchestrator, e))
                    .handler(e -> update())
                    .bindWith(consumer);

            Events.subscribe(LGStageStartedEvent.class)
                    .filter(orchestrator::isMyEvent)
                    .handler(e -> update())
                    .bindWith(consumer);
        }
    }
}
