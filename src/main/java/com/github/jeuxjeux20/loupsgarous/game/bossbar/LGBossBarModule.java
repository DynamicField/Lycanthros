package com.github.jeuxjeux20.loupsgarous.game.bossbar;

import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;
import com.google.inject.AbstractModule;

public final class LGBossBarModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGBossBarManager.class);
        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.BOSS_BAR,
                        LGBossBarManager.class);
            }
        });
    }
}
