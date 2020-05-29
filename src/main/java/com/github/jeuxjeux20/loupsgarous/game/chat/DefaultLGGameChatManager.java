package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

class DefaultLGGameChatManager implements LGGameChatManager {
    private final Set<LGGameChatChannel> channels;
    private final LoupsGarous plugin;

    @Inject
    DefaultLGGameChatManager(Set<LGGameChatChannel> channels, LoupsGarous plugin) {
        this.channels = new HashSet<>(channels);
        this.plugin = plugin;
    }

    @Override
    public void redirectMessage(LGPlayer sender, String message, LGGameOrchestrator orchestrator) {
        Player senderMinecraftPlayer = sender.getMinecraftPlayer()
                .orElseThrow(() -> new IllegalArgumentException("The sender has no minecraft player."));

        Set<LGGameChatChannel> writableChannels = getWritableChannels(sender, orchestrator);

        if (writableChannels.isEmpty()) {
            senderMinecraftPlayer.sendMessage(ChatColor.RED +
                                              "Vous ne pouvez pas envoyer de message à ce moment de la partie.");
            return;
        }
        if (writableChannels.size() > 1) {
            plugin.getLogger().warning("Il y'a plusieurs channels et c'est choisir 1 est pas encore implémenté:\n" +
                                       writableChannels.stream().map(LGGameChatChannel::getName).collect(Collectors.joining(", ")) +
                                       "\n Seulement le premier sera pris.");
        }
        LGGameChatChannel channel = writableChannels.iterator().next();

        sendMessage(channel, recipient -> buildMessage(sender, message, orchestrator, channel, recipient), orchestrator);
    }

    private String buildMessage(LGPlayer sender, String message, LGGameOrchestrator orchestrator,
                              LGGameChatChannel channel, LGPlayer recipient) {
        StringBuilder messageBuilder = new StringBuilder();

        if (channel.isNameDisplayed()) {
            messageBuilder.append(ChatColor.GRAY)
                    .append('[')
                    .append(channel.getName())
                    .append(ChatColor.GRAY)
                    .append(']')
                    .append(ChatColor.RESET);
        }

        messageBuilder.append('<')
                .append(channel.formatUsername(sender, recipient, orchestrator))
                .append("> ")
                .append(message);

        return messageBuilder.toString();
    }

    @Override
    public void sendMessage(LGGameChatChannel channel, Function<? super LGPlayer, String> messageFunction,
                            LGGameOrchestrator orchestrator) {
        for (LGPlayer player : orchestrator.getGame().getPlayers()) {
            player.getMinecraftPlayer().ifPresent(minecraftPlayer -> {
                if (!channel.areMessagesVisibleTo(player, orchestrator)) return;

                minecraftPlayer.sendMessage(messageFunction.apply(player));
            });
        }
    }

    @Override
    public Set<LGGameChatChannel> getChannels() {
        return channels;
    }
}
