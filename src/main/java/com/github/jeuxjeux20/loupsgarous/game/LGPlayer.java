package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.kill.causes.LGKillCause;
import com.github.jeuxjeux20.loupsgarous.powers.PowerRegistry;
import com.github.jeuxjeux20.loupsgarous.tags.TagRegistry;
import com.github.jeuxjeux20.loupsgarous.teams.TeamRegistry;

public interface LGPlayer extends BasicLGPlayer {
    boolean isDead();

    default boolean isAlive() {
        return !isDead();
    }

    TeamRegistry teams();

    TagRegistry tags();

    PowerRegistry powers();

    void setCard(LGCard card);

    boolean willDie();

    default boolean willNotDie() {
        return !willDie();
    }

    void die(LGKillCause cause);

    void dieLater(LGKillCause cause);

    void cancelFutureDeath();
}
