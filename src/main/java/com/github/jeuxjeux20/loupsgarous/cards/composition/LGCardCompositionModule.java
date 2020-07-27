package com.github.jeuxjeux20.loupsgarous.cards.composition;

import com.github.jeuxjeux20.loupsgarous.cards.composition.gui.LGCompositionGuiModule;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.LGCompositionValidatorsModule;
import com.google.inject.AbstractModule;

public class LGCardCompositionModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGCompositionGuiModule());
        install(new LGCompositionValidatorsModule());
    }
}
