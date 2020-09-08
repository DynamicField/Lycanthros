package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints;
import com.github.jeuxjeux20.loupsgarous.game.AbstractOrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LGChatOrchestrator extends AbstractOrchestratorComponent {
    private final Map<ChatChannel, AnonymizationPool> anonymizationPools = new HashMap<>();

    @Inject
    LGChatOrchestrator(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        registerRedirectionEvents();
    }

    private void registerRedirectionEvents() {
        Events.subscribe(AsyncPlayerChatEvent.class)
                .filter(x -> orchestrator.isGameRunning())
                .handler(this::handlePlayerSendMessage)
                .bindWith(this);
    }

    private void redirectMessage(LGPlayer sender, String message, String format) {
        Player senderMinecraftPlayer = sender.minecraft()
                .orElseThrow(() -> new IllegalArgumentException("The sender has no minecraft player."));

        Map<ChatChannel, ChatChannelView> channels =
                getChannelViews(sender);

        Map<ChatChannel, ChatChannelView> writableChannels =
                Maps.filterEntries(channels, x -> x != null && x.getValue().isWritable());

        if (writableChannels.isEmpty()) {
            senderMinecraftPlayer.sendMessage(ChatColor.RED +
                                              "Vous ne pouvez pas envoyer de message à ce moment de la partie.");
            return;
        }
        if (writableChannels.size() > 1) {
            orchestrator.logger().warning("Using multiple channels is not yet implemented:\n" +
                                          writableChannels.keySet().stream()
                                                  .map(ChatChannel::getDefaultName)
                                                  .collect(Collectors.joining(", ")) +
                                          "\nThe first channel will be used.");
        }

        ChatChannel channel = writableChannels.keySet().iterator().next();

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

    public void sendMessage(ChatChannel channel,
                            Function<? super LGPlayer, ? extends TextComponent> messageFunction) {
        sendMessageInternal(channel,
                (player, minecraftPlayer, view) ->
                        Text.sendMessage(minecraftPlayer, messageFunction.apply(player)));
    }

    public void sendMessage(ChatChannel channel, String message) {
        sendMessage(channel, Text.fromLegacy(message));
    }

    public void sendMessage(ChatChannel channel, TextComponent message) {
        sendMessage(channel, p -> message);
    }

    public Set<ChatChannel> getChannels() {
        return orchestrator.getGameBox().contents(LGExtensionPoints.CHAT_CHANNELS);
    }

    @Override
    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    private void sendMessageInternal(ChatChannel channel, InternalMessageSender messageSender) {
        for (LGPlayer player : orchestrator.getPlayers()) {
            player.minecraft(minecraftPlayer -> {
                ChatContext context = new ChatContext(orchestrator, player);
                ChatChannelView view = channel.getView(context);

                if (view.isReadable()) {
                    messageSender.send(player, minecraftPlayer, view);
                }
            });
        }
    }

    private Map<ChatChannel, ChatChannelView> getChannelViews(LGPlayer player) {
        Map<ChatChannel, ChatChannelView> map = new HashMap<>();
        ChatContext context = new ChatContext(orchestrator, player);

        for (ChatChannel channel : getChannels()) {
            ChatChannelView view = channel.getView(context);
            map.put(channel, view);
        }

        return map;
    }

    public void sendToEveryone(String message) {
        getOrchestrator().getAllMinecraftPlayers().forEach(player -> player.sendMessage(message));
    }

    private void handlePlayerSendMessage(AsyncPlayerChatEvent event) {
        orchestrator.getPlayer(event.getPlayer().getUniqueId()).ifPresent(player -> {
            event.setCancelled(true);

            String message = event.getMessage();
            String format = event.getFormat();

            Schedulers.sync().run(() -> this.redirectMessage(player, message, format));
        });
    }

    private interface InternalMessageSender {
        void send(LGPlayer recipient, Player minecraftRecipient, ChatChannelView playerChatChannelView);
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
