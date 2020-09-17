package com.github.jeuxjeux20.loupsgarous.tags;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import org.bukkit.ChatColor;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.TAG_REVELATION_MECHANICS;

public abstract class LGTag {
    public abstract String getName();

    public abstract ChatColor getColor();

    public final boolean isRevealed(LGGameOrchestrator orchestrator,
                                    LGPlayer holder, LGPlayer viewer) {
        TagRevelationContext context =
                new TagRevelationContext(orchestrator, holder, viewer, this);

        if (!holder.tags().has(this)) {
            return false;
        }

        setupRevelation(context);

        for (TagRevelationMechanic revelationMechanic :
                orchestrator.getGameBox().contents(TAG_REVELATION_MECHANICS)) {
            if (revelationMechanic.handlesTag(this) &&
                (!context.isRevealed() || revelationMechanic.canHide())) {
                revelationMechanic.execute(context);
            }
        }

        return context.isRevealed();
    }

    protected void setupRevelation(TagRevelationContext context) {
        context.hide();
    }
}
