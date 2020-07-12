package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.cards.AnonymousNameHolder;

public interface AnonymizedNamesProvider {
    default String createAnonymousNameOrGet(AnonymousNameHolder anonymousNameHolder) {
        if (anonymousNameHolder.getAnonymizedName() != null)
            return anonymousNameHolder.getAnonymizedName();

        return createAnonymousName(anonymousNameHolder);
    }

    String createAnonymousName(AnonymousNameHolder anonymousNameHolder);

    interface Factory {
        AnonymizedNamesProvider create(String[] names);
    }
}
