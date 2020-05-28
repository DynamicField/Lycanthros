package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.stages.TimedStage;
import me.lucko.helper.time.DurationFormatter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class MinecraftLGActionBarManager implements LGActionBarManager {
    @Override
    public void update(LGPlayer player, LGGameOrchestrator orchestrator) {
        player.getMinecraftPlayer().ifPresent(minecraftPlayer -> {
            List<BaseComponent> components = new ArrayList<>();

            renderParts(orchestrator, components, player,
                    this::createStateComponents,
                    this::createTimeComponents);

            BaseComponent[] componentsArray = components.toArray(new BaseComponent[0]);

            minecraftPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, componentsArray);
        });
    }

    private List<BaseComponent> createTimeComponents(LGGameOrchestrator orchestrator, LGPlayer player) {
        List<BaseComponent> components = new ArrayList<>();

        if (!orchestrator.lobby().isLocked()) {
            TextComponent slotsComponent = new TextComponent(orchestrator.lobby().getSlotsDisplay());
            slotsComponent.setBold(true);
            slotsComponent.setColor(ChatColor.GREEN);

            components.add(slotsComponent);
        } else if (orchestrator.getState() == LGGameState.STARTED) {
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

    private List<BaseComponent> createStateComponents(LGGameOrchestrator orchestrator, LGPlayer player) {
        List<BaseComponent> components = new ArrayList<>();

        if (orchestrator.getState() == LGGameState.WAITING_FOR_PLAYERS) {
            components.add(new TextComponent("En attente"));
        } else if (orchestrator.getState() == LGGameState.READY_TO_START) {
            components.add(new TextComponent("Départ dans "));

            Optional<TimedStage> maybeTimedStage = orchestrator.stages().current().getComponent(TimedStage.class);

            TextComponent numberComponent = maybeTimedStage.map(this::numberComponent)
                    .orElseGet(() -> new TextComponent("?"));
            numberComponent.setColor(ChatColor.AQUA);
            numberComponent.setBold(true);

            components.add(numberComponent);

            components.add(new TextComponent(" secondes"));
        } else if (orchestrator.getState() == LGGameState.STARTED) {
            components.add(new TextComponent("Vous êtes : "));

            LGCard card = player.getCard();

            TextComponent cardComponent = new TextComponent(card.getName());
            cardComponent.setColor(card.getColor().asBungee());
            cardComponent.setBold(true);

            components.add(cardComponent);
        } else if (orchestrator.getState() == LGGameState.FINISHED) {
            LGEnding ending = orchestrator.getEnding()
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

    private void renderParts(LGGameOrchestrator orchestrator, List<BaseComponent> components, LGPlayer player,
                             Part... parts) {
        for (int i = 0; i < parts.length; i++) {
            Part part = parts[i];

            List<BaseComponent> partComponents = part.render(orchestrator, player);

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
        List<BaseComponent> render(LGGameOrchestrator orchestrator, LGPlayer player);
    }
}
