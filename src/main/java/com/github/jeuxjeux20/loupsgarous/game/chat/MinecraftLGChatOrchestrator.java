package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

class MinecraftLGChatOrchestrator implements LGChatOrchestrator {
    private final LGGameOrchestrator orchestrator;
    private final Set<LGChatChannel> channels;
    private final LoupsGarous plugin;

    @Inject
    MinecraftLGChatOrchestrator(@Assisted LGGameOrchestrator orchestrator, Set<LGChatChannel> channels, LoupsGarous plugin) {
        this.orchestrator = orchestrator;
        this.channels = new HashSet<>(channels);
        this.plugin = plugin;
    }

    @Override
    public void redirectMessage(LGPlayer sender, String message) {
        Player senderMinecraftPlayer = sender.getMinecraftPlayer()
                .orElseThrow(() -> new IllegalArgumentException("The sender has no minecraft player."));

        Set<LGChatChannel> writableChannels = getWritableChannels(sender);

        if (writableChannels.isEmpty()) {
            senderMinecraftPlayer.sendMessage(ChatColor.RED +
                                              "Vous ne pouvez pas envoyer de message à ce moment de la partie.");
            return;
        }
        if (writableChannels.size() > 1) {
            plugin.getLogger().warning("Il y'a plusieurs channels et c'est choisir 1 est pas encore implémenté:\n" +
                                       writableChannels.stream().map(LGChatChannel::getName).collect(Collectors.joining(", ")) +
                                       "\n Seulement le premier sera pris.");
        }
        LGChatChannel channel = writableChannels.iterator().next();

        sendMessage(channel, recipient -> buildMessage(sender, message, channel, recipient));
    }

    private String buildMessage(LGPlayer sender, String message, LGChatChannel channel, LGPlayer recipient) {
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
    public void sendMessage(LGChatChannel channel, Function<? super LGPlayer, String> messageFunction) {
        for (LGPlayer player : orchestrator.game().getPlayers()) {
            player.getMinecraftPlayer().ifPresent(minecraftPlayer -> {
                if (!channel.areMessagesVisibleTo(player, orchestrator)) return;

                minecraftPlayer.sendMessage(messageFunction.apply(player));
            });
        }
    }

    @Override
    public Set<LGChatChannel> getChannels() {
        return channels;
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }
}
