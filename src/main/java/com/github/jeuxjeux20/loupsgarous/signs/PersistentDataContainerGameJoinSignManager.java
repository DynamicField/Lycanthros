package com.github.jeuxjeux20.loupsgarous.signs;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

@Singleton
final class PersistentDataContainerGameJoinSignManager implements GameJoinSignManager {
    private final NamespacedKey signDataKey;

    @Inject
    PersistentDataContainerGameJoinSignManager(LoupsGarous plugin) {
        this.signDataKey = new NamespacedKey(plugin, "game_name");
    }

    @Override
    public void updateSignData(Sign sign, String gameName) {
        sign.getPersistentDataContainer().set(signDataKey, PersistentDataType.STRING, gameName);
        sign.update();
    }

    @Override
    public void deleteSignData(Sign sign) {
        sign.getPersistentDataContainer().remove(signDataKey);
    }

    @Override
    public Optional<String> getSignGameName(Sign sign) {
        return Optional.ofNullable(sign.getPersistentDataContainer().get(signDataKey, PersistentDataType.STRING));
    }
}
