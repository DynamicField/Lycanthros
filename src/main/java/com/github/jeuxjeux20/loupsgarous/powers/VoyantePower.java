package com.github.jeuxjeux20.loupsgarous.powers;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.reflect.TypeToken;
import me.lucko.helper.metadata.MetadataKey;

import java.util.Set;

public final class VoyantePower extends LGPower {
    public static final MetadataKey<Set<LGPlayer>> PLAYERS_SAW_KEY =
            MetadataKey.create("voyante_players_saw", new TypeToken<Set<LGPlayer>>(){});

    public VoyantePower(Object source) {
        super(source);
    }

    @Override
    public String getName() {
        return "Pouvoir de la voyante";
    }
}
