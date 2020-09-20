package com.github.jeuxjeux20.loupsgarous.actionbar;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.CompositionValidator.Problem;
import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.event.CountdownTickEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGCompositionUpdateEvent;
import com.github.jeuxjeux20.loupsgarous.event.phase.LGPhaseStartedEvent;
import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.phases.LobbyPhase;
import com.github.jeuxjeux20.loupsgarous.phases.PhaseEventUtils;
import com.github.jeuxjeux20.loupsgarous.phases.TimedPhase;
import com.google.inject.Inject;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
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

public class LGActionBarManager extends AbstractOrchestratorComponent {
    @Inject
    LGActionBarManager(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        bindModule(new UpdateModule());
    }

    public void update() {
        for (LGPlayer player : orchestrator.getPlayers()) {
            update(player);
        }
    }

    private void update(LGPlayer player) {
        player.minecraft(minecraftPlayer -> {
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

        if (orchestrator.getState() == LGGameState.LOBBY) {
            LobbyPhase lobbyPhase = (LobbyPhase) orchestrator.phases().current();

            if (lobbyPhase.isStarting()) {
                components.add(new TextComponent("Départ dans "));

                TextComponent numberComponent = new TextComponent(formatTimeLeft(lobbyPhase));
                numberComponent.setColor(ChatColor.AQUA);
                numberComponent.setBold(true);

                components.add(numberComponent);
            } else {
                components.add(new TextComponent("En attente"));
            }

            addCompositionProblemComponent(components);
        } else if (orchestrator.getState() == LGGameState.STARTED) {
            components.add(new TextComponent("Vous êtes : "));

            LGCard card = player.getCard();

            TextComponent cardComponent = new TextComponent(card.getName());
            cardComponent.setColor(card.getColor().asBungee());
            cardComponent.setBold(true);

            components.add(cardComponent);
        } else if (orchestrator.getState() == LGGameState.FINISHED) {
            LGEnding ending = orchestrator.getEnding();
            assert ending != null;

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
        LobbyPhase lobbyPhase = (LobbyPhase) orchestrator.phases().current();

        if (lobbyPhase.getWorstCompositionProblemType() == Problem.Type.IMPOSSIBLE) {
            TextComponent component = new TextComponent(" (Composition invalide !)");
            component.setColor(ChatColor.RED);
            components.add(component);
        } else if (lobbyPhase.getWorstCompositionProblemType() == Problem.Type.RULE_BREAKING) {
            TextComponent component = new TextComponent(" (Composition contre les règles)");
            component.setColor(ChatColor.YELLOW);
            components.add(component);
        }
    }

    private List<BaseComponent> createTimeComponents(LGPlayer player) {
        List<BaseComponent> components = new ArrayList<>();

        if (orchestrator.allowsJoin()) {
            TextComponent slotsComponent = new TextComponent(orchestrator.getSlotsDisplay());
            slotsComponent.setBold(true);
            slotsComponent.setColor(ChatColor.GREEN);

            components.add(slotsComponent);
        } else if (orchestrator.getState() == LGGameState.STARTED) {
            orchestrator.phases().current().safeCast(TimedPhase.class).ifPresent(timedPhase -> {
                String formattedDuration = formatTimeLeft(timedPhase);

                TextComponent timeComponent = new TextComponent(formattedDuration);
                timeComponent.setBold(true);
                timeComponent.setColor(ChatColor.GREEN);

                components.add(timeComponent);
            });
        }

        return components;
    }

    private String formatTimeLeft(TimedPhase timedPhase) {
        Duration secondsLeftDuration = Duration.ofSeconds(timedPhase.getSecondsLeft());
        return DurationFormatter.CONCISE.format(secondsLeftDuration);
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

    @FunctionalInterface
    private interface Part {
        List<BaseComponent> render(LGPlayer player);
    }

    private final class UpdateModule implements TerminableModule {
        @Override
        public void setup(@Nonnull TerminableConsumer consumer) {
            Events.subscribe(CountdownTickEvent.class)
                    .filter(e -> PhaseEventUtils.isCurrentPhaseCountdownEvent(orchestrator, e))
                    .handler(e -> update())
                    .bindWith(consumer);

            Events.merge(LGEvent.class, LGPhaseStartedEvent.class, LGCompositionUpdateEvent.class)
                    .filter(orchestrator::isMyEvent)
                    .handler(e -> update())
                    .bindWith(consumer);

            Schedulers.sync().runRepeating((Runnable) LGActionBarManager.this::update, 0L, 5L);
        }
    }
}
