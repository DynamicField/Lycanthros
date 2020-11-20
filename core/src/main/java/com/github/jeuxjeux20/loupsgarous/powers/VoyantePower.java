package com.github.jeuxjeux20.loupsgarous.powers;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.storage.ReadonlyStorageProperty;
import com.github.jeuxjeux20.loupsgarous.storage.StorageKey;
import com.github.jeuxjeux20.loupsgarous.storage.StorageProvider;

import java.util.HashSet;
import java.util.Set;

public final class VoyantePower extends LGPower {
    public static final ReadonlyStorageProperty<Set<LGPlayer>> PLAYERS_SAW_PROPERTY =
            new ReadonlyStorageProperty<Set<LGPlayer>>(new StorageKey<>("voyante_players_saw")) {
                @Override
                public Set<LGPlayer> get(StorageProvider provider) {
                    return provider.getStorage().getOrPut(getKey(), HashSet::new);
                }
            };

    public VoyantePower(Object source) {
        super(source);
    }

    @Override
    public String getName() {
        return "Pouvoir de la voyante";
    }
}
