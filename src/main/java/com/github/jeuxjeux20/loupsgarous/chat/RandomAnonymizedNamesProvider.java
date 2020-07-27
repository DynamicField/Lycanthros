package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class RandomAnonymizedNamesProvider implements AnonymizedNamesProvider {
    private static final Random random = new Random();

    private final List<String> names;

    @Inject
    RandomAnonymizedNamesProvider(@Assisted String[] names) {
        this.names = new ArrayList<>(Arrays.asList(names));
    }

    @Override
    public String updateAnonymousName(LGPlayer player) {
        String name;
        if (names.isEmpty()) {
            name = String.valueOf(random.nextInt(100000));
        } else {
            name = names.remove(random.nextInt(names.size()));
        }
        ANONYMIZED_NAME.set(player, name);
        return name;
    }
}
