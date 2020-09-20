package com.github.jeuxjeux20.loupsgarous.kill.causes;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class SingleLGKillCause extends LGKillCause {
    @Override
    public String getKillMessage(Set<LGPlayer> players) {
        if (players.size() == 0) {
            return "";
        } else if (players.size() == 1) {
            return getKillMessage(players.iterator().next());
        } else {
            Bukkit.getLogger().warning(
                    "[LoupsGarous] " + getClass().getSimpleName() + "has been called with multiple players, " +
                    "but it is a SingleLGKillCause.");

            return players.stream().map(this::getKillMessage).collect(Collectors.joining(ChatColor.RESET + "\n"));
        }
    }

    protected abstract String getKillMessage(LGPlayer player);
}
