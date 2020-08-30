package com.github.jeuxjeux20.loupsgarous.cards.composition;

import com.github.jeuxjeux20.loupsgarous.cards.composition.gui.LGCompositionGuiModule;
import com.google.inject.AbstractModule;

public class LGCardCompositionModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGCompositionGuiModule());

    }
}
