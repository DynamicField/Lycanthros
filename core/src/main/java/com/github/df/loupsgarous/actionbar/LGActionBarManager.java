package com.github.df.loupsgarous.actionbar;

import com.github.df.loupsgarous.cards.LGCard;
import com.github.df.loupsgarous.cards.composition.validation.CompositionValidator.Problem;
import com.github.df.loupsgarous.endings.LGEnding;
import com.github.df.loupsgarous.event.LGEvent;
import com.github.df.loupsgarous.event.lobby.LGCompositionChangeEvent;
import com.github.df.loupsgarous.event.phase.LGPhaseStartedEvent;
import com.github.df.loupsgarous.game.*;
import com.github.df.loupsgarous.game.*;
import com.github.df.loupsgarous.phases.CountdownTimedPhase;
import com.github.df.loupsgarous.phases.LobbyPhase;
import com.github.df.loupsgarous.phases.Phase;
import com.github.df.loupsgarous.phases.TimedPhase;
import io.reactivex.rxjava3.disposables.Disposable;
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

public class LGActionBarManager extends OrchestratorComponent {
    public LGActionBarManager(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected void onStart() {
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
                    components.add(new TextComponent(
                            "Vous avez... " + PlayerGameOutcome.SHRUG_EMOJI));
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
            Phase phase = orchestrator.phases().current();
            if (phase instanceof TimedPhase) {
                String formattedDuration = formatTimeLeft(((TimedPhase) phase));

                TextComponent timeComponent = new TextComponent(formattedDuration);
                timeComponent.setBold(true);
                timeComponent.setColor(ChatColor.GREEN);

                components.add(timeComponent);
            }
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
            Disposable subscription = orchestrator.phases().currentUpdates()
                    .compose(CountdownTimedPhase::notifyOnTick)
                    .subscribe(x -> update());

            consumer.bind(subscription::dispose);

            Events.merge(LGEvent.class, LGPhaseStartedEvent.class, LGCompositionChangeEvent.class)
                    .filter(orchestrator::isMyEvent)
                    .handler(e -> update())
                    .bindWith(consumer);

            Schedulers.sync().runRepeating((Runnable) LGActionBarManager.this::update, 0L, 5L)
                    .bindWith(consumer);
        }
    }
}
