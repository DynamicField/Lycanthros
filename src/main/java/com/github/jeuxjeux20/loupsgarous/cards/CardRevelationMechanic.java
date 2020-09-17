package com.github.jeuxjeux20.loupsgarous.cards;

import com.github.jeuxjeux20.loupsgarous.Mechanic;

public interface CardRevelationMechanic extends Mechanic {
    boolean canHide();

    void execute(CardRevelationContext context);
}
