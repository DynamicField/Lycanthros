package com.github.jeuxjeux20.loupsgarous.game.cards.composition;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.gui.LGCompositionGuiModule;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.LGCompositionValidatorsModule;
import com.google.inject.AbstractModule;

public class LGCompositionModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGCompositionGuiModule());
        install(new LGCompositionValidatorsModule());
    }
}
