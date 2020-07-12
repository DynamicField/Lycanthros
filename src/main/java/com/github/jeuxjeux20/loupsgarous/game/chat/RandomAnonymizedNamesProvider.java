package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.cards.AnonymousNameHolder;
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
    public String createAnonymousName(AnonymousNameHolder anonymousNameHolder) {
        if (anonymousNameHolder.getAnonymizedName() != null)
            return anonymousNameHolder.getAnonymizedName();

        String name;
        if (names.isEmpty()) {
            name = String.valueOf(random.nextInt(100000));
        } else {
            name = names.remove(random.nextInt(names.size()));
        }
        anonymousNameHolder.setAnonymizedName(name);
        return name;
    }
}
