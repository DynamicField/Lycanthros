package com.github.df.loupsgarous.chat;

import com.github.df.loupsgarous.extensibility.registry.GameRegistries;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ActualChatOrchestrator implements Terminable, ChatOrchestrator {
    private final Map<ChatChannel, AnonymizationPool> anonymizationPools = new HashMap<>();
    private final LGGameOrchestrator orchestrator;
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();

    public ActualChatOrchestrator(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;

        registerRedirectionEvents();
    }

    private void registerRedirectionEvents() {
        Events.subscribe(AsyncPlayerChatEvent.class)
                .filter(x -> orchestrator.isGameRunning())
                .handler(this::handlePlayerSendMessage)
                .bindWith(terminableRegistry);
    }

    private void redirectMessage(LGPlayer sender, String message, String format) {
        Player senderMinecraftPlayer = sender.minecraft()
                .orElseThrow(
                        () -> new IllegalArgumentException("The sender has no minecraft player."));

        Set<ChatChannelView> channels =
                getChannelViews(sender);

        Set<ChatChannelView> writableChannels =
                channels.stream().filter(ChatChannelView::isWritable).collect(Collectors.toSet());

        if (writableChannels.isEmpty()) {
            senderMinecraftPlayer.sendMessage(ChatColor.RED +
                                              "Vous ne pouvez pas envoyer de message à ce moment de la partie.");
            return;
        }
        if (writableChannels.size() > 1) {
            orchestrator.logger().warning("Using multiple channels is not yet implemented:\n" +
                                          writableChannels.stream()
                                                  .map(x -> x.getChatChannel().getId())
                                                  .collect(Collectors.joining(", ")) +
                                          "\nThe first channel will be used.");
        }

        ChatChannel channel = writableChannels.iterator().next().getChatChannel();

        sendMessageInternal(channel, (recipient, minecraftRecipient, props) -> {
            String redirectedMessage = buildRedirectedMessage(sender, recipient, message,
                    props, format);

            minecraftRecipient.sendMessage(redirectedMessage);
        });
    }

    private String buildRedirectedMessage(LGPlayer sender,
            LGPlayer recipient,
            String message,
            ChatChannelView channelView,
            String format) {
        if (channelView.isNameDisplayed()) {
            format = ChatColor.GRAY + "[" + channelView.getName() + "]" +
                     ChatColor.RESET + format;
        }

        String username;
        if (channelView.isSenderAnonymized()) {
            AnonymizationPool anonymizationPool =
                    anonymizationPools.computeIfAbsent(channelView.getChatChannel(),
                            c -> new AnonymizationPool(channelView.getAnonymizedNames()));

            username = anonymizationPool.giveName(recipient);
        } else {
            username = sender.getName();
        }

        return String.format(format, username, message);
    }

    @Override
    public void sendMessage(ChatChannel channel,
            Function<? super LGPlayer, ? extends TextComponent> messageFunction) {
        sendMessageInternal(channel,
                (player, minecraftPlayer, view) ->
                        Text.sendMessage(minecraftPlayer, messageFunction.apply(player)));
    }

    @Override
    public void sendMessage(ChatChannel channel, String message) {
        sendMessage(channel, Text.fromLegacy(message));
    }

    @Override
    public void sendMessage(ChatChannel channel, TextComponent message) {
        sendMessage(channel, p -> message);
    }

    @Override
    public ImmutableSet<ChatChannel> getChannels() {
        return GameRegistries.CHAT_CHANNELS.get(orchestrator).getValues();
    }

    private void sendMessageInternal(ChatChannel channel, InternalMessageSender messageSender) {
        for (LGPlayer player : orchestrator.getPlayers()) {
            player.minecraft(minecraftPlayer -> {
                ChatChannelView view = channel.getView(player);

                if (view.isReadable()) {
                    messageSender.send(player, minecraftPlayer, view);
                }
            });
        }
    }

    private Set<ChatChannelView> getChannelViews(LGPlayer player) {
        return getChannels().stream()
                .map(x -> x.getView(player))
                .collect(Collectors.toSet());
    }

    @Override
    public void sendToEveryone(String message) {
        orchestrator.getAllMinecraftPlayers().forEach(player -> player.sendMessage(message));
    }

    private void handlePlayerSendMessage(AsyncPlayerChatEvent event) {
        orchestrator.getPlayer(event.getPlayer().getUniqueId()).ifPresent(player -> {
            event.setCancelled(true);

            String message = event.getMessage();
            String format = event.getFormat();

            Schedulers.sync().run(() -> this.redirectMessage(player, message, format));
        });
    }

    @Override
    public void close() throws Exception {
        terminableRegistry.close();
    }

    private interface InternalMessageSender {
        void send(LGPlayer recipient, Player minecraftRecipient, ChatChannelView playerChannelView);
    }

    private static class AnonymizationPool {
        private final Collection<String> allNames;
        private final List<String> availableNames;
        private final Map<LGPlayer, String> assignedNames = new HashMap<>();
        private int nameResetCounter = 0;

        private AnonymizationPool(Collection<String> anonymizedNames) {
            this.allNames = anonymizedNames;
            this.availableNames = new ArrayList<>(anonymizedNames);
        }

        public String giveName(LGPlayer player) {
            return assignedNames.computeIfAbsent(player, p -> findRandomName());
        }

        /**
         * Finds a random name and appends a number to avoid duplicates when necessary.
         *
         * @return the random name
         */
        private String findRandomName() {
            if (allNames.isEmpty()) {
                return String.valueOf(++nameResetCounter);
            }

            if (availableNames.isEmpty()) {
                availableNames.addAll(allNames);
                nameResetCounter++;
            }

            String name = takeRandomAvailableName();
            if (nameResetCounter > 0) {
                name += " " + nameResetCounter + 1;
            }

            return name;
        }

        private String takeRandomAvailableName() {
            int index = RandomUtils.nextInt(availableNames.size());
            String value = availableNames.get(index);
            availableNames.remove(index);
            return value;
        }
    }
}
