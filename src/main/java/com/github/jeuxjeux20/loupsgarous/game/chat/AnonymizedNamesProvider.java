package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.MetadataExtension;
import me.lucko.helper.metadata.MetadataKey;
import org.jetbrains.annotations.Nullable;

public interface AnonymizedNamesProvider {
    MetadataExtension<LGPlayer, @Nullable String> ANONYMIZED_NAME =
            MetadataExtension.create(MetadataKey.create("anonymized_name", String.class));

    default String updateAnonymousNameOrGet(LGPlayer player) {
        String anonymousName = ANONYMIZED_NAME.get(player);
        if (anonymousName != null)
            return anonymousName;

        return updateAnonymousName(player);
    }

    String updateAnonymousName(LGPlayer player);

    interface Factory {
        AnonymizedNamesProvider create(String[] names);
    }
}
