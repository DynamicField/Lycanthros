package com.github.jeuxjeux20.loupsgarous.teams;

public interface TeamRevelationMechanic {
    boolean handlesTeam(LGTeam team);

    boolean canHide();

    void execute(TeamRevelationContext context);
}
