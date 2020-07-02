package com.github.jeuxjeux20.loupsgarous.game.tags.revealers;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class TagRevealersModule extends AbstractModule {
    private @Nullable Multibinder<TagRevealer> tagRevealerBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureTagRevealers();
    }

    protected void configureBindings() {
    }

    protected void configureTagRevealers() {
    }

    private void actualConfigureTagRevealers() {
        tagRevealerBinder = Multibinder.newSetBinder(binder(), TagRevealer.class);

        configureTagRevealers();
    }

    protected final void addTagRevealer(Class<? extends TagRevealer> tagRevealer) {
        addTagRevealer(TypeLiteral.get(tagRevealer));
    }

    protected final void addTagRevealer(TypeLiteral<? extends TagRevealer> tagRevealer) {
        Preconditions.checkState(tagRevealerBinder != null, "addTagRevealer can only be used inside configureTagRevealers()");

        tagRevealerBinder.addBinding().to(tagRevealer);
    }
}
