package com.github.df.loupsgarous.lobby;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import me.lucko.helper.terminable.Terminable;

public interface TerminableMultiverseWorld extends Terminable {
    MultiverseWorld get();
}
