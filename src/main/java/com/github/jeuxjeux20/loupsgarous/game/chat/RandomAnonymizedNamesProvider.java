package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.cards.AnonymousNameHolder;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;

class RandomAnonymizedNamesProvider implements AnonymizedNamesProvider {
    private static final Random random = new Random();

    @Override
    public String createAnonymousName(LGGameOrchestrator orchestrator, AnonymousNameHolder anonymousNameHolder,
                                      AnonymizedChatChannel chatChannel, String[] names) {
        if (anonymousNameHolder.getAnonymizedName() != null)
            return anonymousNameHolder.getAnonymizedName();

        List<String> availableNames = orchestrator.getAnonymizedNames().computeIfAbsent(chatChannel,
                k -> Lists.newArrayList(names));

        String name;
        if (availableNames.isEmpty()) {
            name = String.valueOf(random.nextInt(100000));
        } else {
            name = availableNames.remove(random.nextInt(availableNames.size()));
        }
        anonymousNameHolder.setAnonymizedName(name);
        return name;
    }
}
