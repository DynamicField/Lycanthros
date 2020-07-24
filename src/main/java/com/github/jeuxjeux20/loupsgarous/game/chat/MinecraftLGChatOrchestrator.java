package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.AbstractOrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.google.inject.Inject;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@OrchestratorScoped
class MinecraftLGChatOrchestrator
        extends AbstractOrchestratorComponent
        implements LGChatOrchestrator {
    private final Set<LGChatChannel> channels;
    private final LoupsGarous plugin;

    @Inject
    MinecraftLGChatOrchestrator(LGGameOrchestrator orchestrator, Set<LGChatChannel> channels, LoupsGarous plugin) {
        super(orchestrator);
        this.channels = new HashSet<>(channels);
        this.plugin = plugin;
    }

    @Override
    public void redirectMessage(LGPlayer sender, String message, String format) {
        Player senderMinecraftPlayer = sender.getMinecraftPlayer()
                .orElseThrow(() -> new IllegalArgumentException("The sender has no minecraft player."));

        Set<LGChatChannel> writableChannels = getWritableChannels(sender);

        if (writableChannels.isEmpty()) {
            senderMinecraftPlayer.sendMessage(ChatColor.RED +
                                              "Vous ne pouvez pas envoyer de message à ce moment de la partie.");
            return;
        }
        if (writableChannels.size() > 1) {
            plugin.getLogger().warning("Using multiple channels is not yet implemented:\n" +
                                       writableChannels.stream().map(LGChatChannel::getName).collect(Collectors.joining(", ")) +
                                       "\n The first channel will be used.");
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

    @Override
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

    @Override
    public Set<LGChatChannel> getChannels() {
        return channels;
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }
}
