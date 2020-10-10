package com.github.jeuxjeux20.loupsgarous.mechanic;

import java.util.List;

public interface MechanicModifierSource {
    List<MechanicModifier> get(MechanicRequest request);
}
