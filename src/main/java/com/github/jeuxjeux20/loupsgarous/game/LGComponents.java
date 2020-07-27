package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.actionbar.LGActionBarManager;
import com.github.jeuxjeux20.loupsgarous.bossbar.LGBossBarManager;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatOrchestrator;
import com.github.jeuxjeux20.loupsgarous.interaction.InteractableRegistry;
import com.github.jeuxjeux20.loupsgarous.inventory.LGInventoryManager;
import com.github.jeuxjeux20.loupsgarous.kill.LGKillsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.scoreboard.LGScoreboardManager;
import com.github.jeuxjeux20.loupsgarous.stages.LGStagesOrchestrator;
import me.lucko.helper.metadata.MetadataKey;

public final class LGComponents {
    private LGComponents() {}

    public static final MetadataKey<LGStagesOrchestrator> STAGES =
            MetadataKey.create("stages", LGStagesOrchestrator.class);

    public static final MetadataKey<LGKillsOrchestrator> KILLS =
            MetadataKey.create("kills", LGKillsOrchestrator.class);

    public static final MetadataKey<LGChatOrchestrator> CHAT =
            MetadataKey.create("chat", LGChatOrchestrator.class);

    public static final MetadataKey<InteractableRegistry> INTERACTABLES =
            MetadataKey.create("interactables", InteractableRegistry.class);

    public static final MetadataKey<LGActionBarManager> ACTION_BAR =
            MetadataKey.create("action_bar", LGActionBarManager.class);

    public static final MetadataKey<LGBossBarManager> BOSS_BAR =
            MetadataKey.create("boss_bar", LGBossBarManager.class);

    public static final MetadataKey<LGInventoryManager> INVENTORY =
            MetadataKey.create("inventory", LGInventoryManager.class);

    public static final MetadataKey<LGScoreboardManager> SCOREBOARD =
            MetadataKey.create("scoreboard", LGScoreboardManager.class);
}
