package com.github.jeuxjeux20.loupsgarous.powers;

import com.github.jeuxjeux20.loupsgarous.MetadataExtension;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.reflect.TypeToken;
import me.lucko.helper.metadata.MetadataKey;

import java.util.HashSet;
import java.util.Set;

public final class VoyantePower extends LGPower {
    public static final MetadataExtension<LGPlayer, Set<LGPlayer>> PLAYERS_SAW = 
            new MetadataExtension<LGPlayer, Set<LGPlayer>>() {
                final MetadataKey<Set<LGPlayer>> key =
                        MetadataKey.create("voyante_players_saw", new TypeToken<Set<LGPlayer>>(){});
        
                @Override
                public MetadataKey<Set<LGPlayer>> getKey() {
                    return key;
                }

                @Override
                public Set<LGPlayer> getDefault() {
                    return new HashSet<>();
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
