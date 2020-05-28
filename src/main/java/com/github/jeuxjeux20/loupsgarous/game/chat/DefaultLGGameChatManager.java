package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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

        if (channel instanceof AnonymizedChatChannel) {
            sendMessage(channel, recipient -> {
                StringBuilder messageBuilder = new StringBuilder();
                buildMessage(sender, message, orchestrator, channel, recipient, messageBuilder);
                return messageBuilder.toString();
            }, orchestrator);
        } else {
            StringBuilder messageBuilder = new StringBuilder();
            buildMessage(sender, message, orchestrator, channel, null, messageBuilder);
            sendMessage(channel, messageBuilder.toString(), orchestrator);
        }
    }

    private void buildMessage(LGPlayer sender, String message, LGGameOrchestrator orchestrator,
                              LGGameChatChannel channel, @Nullable LGPlayer recipient, StringBuilder messageBuilder) {
        if (channel.isNameDisplayed()) {
            messageBuilder.append(ChatColor.GRAY)
                    .append('[')
                    .append(channel.getName())
                    .append(ChatColor.GRAY)
                    .append(']')
                    .append(ChatColor.RESET);
        }


        String senderName = recipient != null && channel instanceof AnonymizedChatChannel &&
                            ((AnonymizedChatChannel) channel).shouldAnonymizeTo(recipient, orchestrator) ?
                ((AnonymizedChatChannel) channel).anonymizeName(sender, orchestrator) :
                sender.getName();

        messageBuilder.append('<')
                .append(senderName)
                .append("> ")
                .append(message);
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
