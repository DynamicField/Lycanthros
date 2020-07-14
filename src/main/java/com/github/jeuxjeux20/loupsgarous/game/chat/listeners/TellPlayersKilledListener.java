package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKill;
import com.github.jeuxjeux20.loupsgarous.game.kill.causes.LGKillCause;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.SKULL_SYMBOL;

public class TellPlayersKilledListener implements Listener {
    public static final String KILL_PREFIX = ChatColor.RED + Character.toString(SKULL_SYMBOL) + " ";

    @EventHandler
    public void onLGKill(LGKillEvent event) {
        Map<LGKillCause, List<LGKill>> killsPerReason = event.getKills().stream()
                .collect(Collectors.groupingBy(LGKill::getCause));

        for (Map.Entry<LGKillCause, List<LGKill>> entry : killsPerReason.entrySet()) {
            LGKillCause reason = entry.getKey();
            Set<LGPlayer> players = entry.getValue().stream()
                    .map(LGKill::getVictim)
                    .collect(Collectors.toSet());

            event.getOrchestrator().chat().sendToEveryone(KILL_PREFIX + reason.getKillMessage(players));
        }
    }
}
