package com.github.jeuxjeux20.loupsgarous.interaction;

import com.github.jeuxjeux20.loupsgarous.Check;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatChannels;
import com.github.jeuxjeux20.loupsgarous.chat.ChatChannel;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickEventBase;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.PickConditions;

import java.util.stream.Stream;

public interface Pick<T> extends Interactable {
    PickConditions<T> conditions();

    void pick(LGPlayer picker, T target);

    default boolean isMyEvent(LGPickEventBase event) {
        return event.getPickData().getSource() == this;
    }

    default ChatChannel getInfoMessagesChannel() {
        return LGChatChannels.createPickChannel(this);
    }

    // Useful stuff

    default Stream<LGPlayer> getEligiblePickers() {
        return getOrchestrator().getPlayers().stream().filter(Check.predicate(conditions()::checkPicker));
    }

    default boolean canSomeonePick() {
        return getEligiblePickers().anyMatch(Check.predicate(conditions()::checkPicker));
    }
}
