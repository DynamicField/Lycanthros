package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

import java.util.Collections;
import java.util.List;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.CARDS;

public abstract class CardRule extends Rule {
    private boolean cardAvailable = true;

    public CardRule(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    public abstract LGCard getCard();

    @Override
    public final List<Extension<?>> getExtensions() {
        ExtensionListBuilder builder = Extension.listBuilder();

        builder.addAll(getOtherExtensions());
        if (cardAvailable) {
            builder.extendSingle(CARDS, getCard());
        }

        return builder.build();
    }

    protected List<Extension<?>> getOtherExtensions() {
        return Collections.emptyList();
    }

    public boolean isCardAvailable() {
        return cardAvailable;
    }

    public void setCardAvailable(boolean cardAvailable) {
        if (orchestrator.isGameRunning()) {
            orchestrator.logger().warning(
                    "Tried to set the CardRule card availability while the game was running.");
            return;
        }
        this.cardAvailable = cardAvailable;
        refresh();
    }
}
