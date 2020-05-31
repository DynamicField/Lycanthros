package com.github.jeuxjeux20.loupsgarous.signs;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

@Singleton
public final class GameJoinSignManager {
    private final NamespacedKey signDataKey;

    @Inject
    GameJoinSignManager(LoupsGarous plugin) {
        this.signDataKey = new NamespacedKey(plugin, "game_name");
    }

    public void updateSignData(Sign sign, String gameName) {
        sign.getPersistentDataContainer().set(signDataKey, PersistentDataType.STRING, gameName);
        sign.update();
    }

    public void deleteSignData(Sign sign) {
        sign.getPersistentDataContainer().remove(signDataKey);
    }

    public Optional<String> getSignGameName(Sign sign) {
        return Optional.ofNullable(sign.getPersistentDataContainer().get(signDataKey, PersistentDataType.STRING));
    }
}
