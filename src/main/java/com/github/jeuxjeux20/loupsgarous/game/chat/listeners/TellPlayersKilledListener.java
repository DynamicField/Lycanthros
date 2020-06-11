package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.events.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKill;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.LGKillReason;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.MultiLGKillReason;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.SKULL_SYMBOL;

public class TellPlayersKilledListener implements Listener {
    public static final String KILL_PREFIX = ChatColor.RED + Character.toString(SKULL_SYMBOL) + " ";

    @EventHandler
    public void onLGKill(LGKillEvent event) {
        Map<? extends Class<? extends LGKillReason>, List<LGKill>> killsPerReason
                = event.getKills().stream().collect(Collectors.groupingBy(x -> x.getReason().getClass()));

        for (List<LGKill> killGroup : killsPerReason.values()) {
            LGKillReason reason = killGroup.get(0).getReason(); // Similar reason for all
            if (reason instanceof MultiLGKillReason) {
                event.getOrchestrator().chat()
                        .sendToEveryone(KILL_PREFIX + ((MultiLGKillReason) reason).getKillMessage(killGroup));
            } else {
                for (LGKill kill : killGroup) {
                    event.getOrchestrator().chat().sendToEveryone(KILL_PREFIX + reason.getKillMessage(kill.getWhoDied()));
                }
            }
        }
    }
}
