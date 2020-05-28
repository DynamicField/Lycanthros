package com.github.jeuxjeux20.loupsgarous.game;

import me.lucko.helper.terminable.Terminable;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface LobbyTeleporter extends Terminable {
    World getWorld();

    void teleportPlayerIn(Player player);

    void teleportPlayerOut(Player player);

    interface Factory {
        LobbyTeleporter create(String id) throws CannotCreateLobbyException;
    }
}
