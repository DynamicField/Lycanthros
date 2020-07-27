package com.github.jeuxjeux20.loupsgarous.cards.composition.gui;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class LGCompositionGuiModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(CompositionGui.Factory.class));
    }
}
