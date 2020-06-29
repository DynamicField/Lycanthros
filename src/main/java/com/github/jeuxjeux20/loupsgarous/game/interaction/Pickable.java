package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.GenericPickChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEventBase;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.util.Check;

import java.util.stream.Stream;

public interface Pickable<T> extends Interactable {
    PickConditions<T> conditions();

    void pick(LGPlayer picker, T target);

    default boolean isMyEvent(LGPickEventBase event) {
        return event.getPick().getEntry().getValue() == this;
    }

    default LGChatChannel getInfoMessagesChannel() {
        return new GenericPickChannel<>(this);
    }

    // Useful stuff

    default Stream<LGPlayer> getEligiblePickers(LGGameOrchestrator orchestrator) {
        return orchestrator.game().getPlayers().stream().filter(Check.predicate(conditions()::checkPicker));
    }

    default boolean canSomeonePick(LGGameOrchestrator orchestrator) {
        return getEligiblePickers(orchestrator).anyMatch(Check.predicate(conditions()::checkPicker));
    }
}
