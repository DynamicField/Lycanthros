package com.github.jeuxjeux20.loupsgarous.signs;

import org.bukkit.block.Sign;

import java.util.Optional;

public interface GameJoinSignManager {
    void updateSignData(Sign sign, String gameName);

    void deleteSignData(Sign sign);

    Optional<String> getSignGameName(Sign sign);
}
