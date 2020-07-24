package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.AbstractOrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.google.inject.Inject;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@OrchestratorScoped
public class LGChatOrchestrator extends AbstractOrchestratorComponent {
    private final Set<LGChatChannel> channels;

    @Inject
    LGChatOrchestrator(LGGameOrchestrator orchestrator, Set<LGChatChannel> channels) {
        super(orchestrator);
        this.channels = new HashSet<>(channels);

        registerRedirectionEvents();
    }

    private void registerRedirectionEvents() {
        Events.subscribe(AsyncPlayerChatEvent.class)
                .handler(this::handlePlayerSendMessage)
                .bindWith(this);
    }

    private void redirectMessage(LGPlayer sender, String message, String format) {
        Player senderMinecraftPlayer = sender.getMinecraftPlayer()
                .orElseThrow(() -> new IllegalArgumentException("The sender has no minecraft player."));

        Set<LGChatChannel> writableChannels = getWritableChannels(sender);

        if (writableChannels.isEmpty()) {
            senderMinecraftPlayer.sendMessage(ChatColor.RED +
                                              "Vous ne pouvez pas envoyer de message à ce moment de la partie.");
            return;
        }
        if (writableChannels.size() > 1) {
            orchestrator.logger().warning("Using multiple channels is not yet implemented:\n" +
                                          writableChannels.stream().map(LGChatChannel::getName).collect(Collectors.joining(", ")) +
                                          "\nThe first channel will be used.");
        }
        LGChatChannel channel = writableChannels.iterator().next();

        sendMessageInternal(channel, (player, minecraftPlayer) -> {
            String redirectedMessage = buildRedirectedMessage(sender, player, message, channel, format);

            minecraftPlayer.sendMessage(redirectedMessage);
        });
    }

    private String buildRedirectedMessage(LGPlayer sender, LGPlayer recipient, String message,
                                          LGChatChannel channel, String format) {
        if (channel.isNameDisplayed()) {
            format = ChatColor.GRAY + "[" + channel.getName() + "]" +
                     ChatColor.RESET + format;
        }

        String username = channel.formatUsername(sender, recipient);

        return String.format(format, username, message);
    }

    public void sendMessage(LGChatChannel channel, Function<? super LGPlayer, ? extends TextComponent> messageFunction) {
        sendMessageInternal(channel,
                (player, minecraftPlayer) -> Text.sendMessage(minecraftPlayer, messageFunction.apply(player)));
    }

    private void sendMessageInternal(LGChatChannel channel, BiConsumer<? super LGPlayer, ? super Player> messageSender) {
        for (LGPlayer player : orchestrator.game().getPlayers()) {
            player.getMinecraftPlayer().ifPresent(minecraftPlayer -> {
                if (!channel.isReadable(player)) return;

                messageSender.accept(player, minecraftPlayer);
            });
        }
    }

    public Set<LGChatChannel> getChannels() {
        return channels;
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }

    public void sendMessage(LGChatChannel channel, String message) {
        sendMessage(channel, Text.fromLegacy(message));
    }

    public void sendMessage(LGChatChannel channel, TextComponent message) {
        sendMessage(channel, p -> message);
    }

    public Set<LGChatChannel> getWritableChannels(LGPlayer sender) {
        HashSet<LGChatChannel> channels = new HashSet<>();
        for (LGChatChannel channel : getChannels()) {
            if (channel.isWritable(sender)) {
                channels.add(channel);
            }
        }
        return channels;
    }

    public void sendToEveryone(String message) {
        gameOrchestrator().getAllMinecraftPlayers().forEach(player -> player.sendMessage(message));
    }

    private void handlePlayerSendMessage(AsyncPlayerChatEvent event) {
        orchestrator.game().getPlayer(event.getPlayer().getUniqueId()).ifPresent(player -> {
            event.setCancelled(true);

            String message = event.getMessage();
            String format = event.getFormat();

            Schedulers.sync().run(() -> orchestrator.chat().redirectMessage(player, message, format));
        });
    }
}
