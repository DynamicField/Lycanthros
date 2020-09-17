package com.github.jeuxjeux20.loupsgarous.tags;

import com.github.jeuxjeux20.loupsgarous.Mechanic;

public interface TagRevelationMechanic extends Mechanic {
    boolean handlesTag(LGTag tag);

    boolean canHide();

    void execute(TagRevelationContext context);
}
