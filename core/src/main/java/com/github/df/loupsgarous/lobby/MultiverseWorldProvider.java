package com.github.df.loupsgarous.lobby;

import com.google.inject.throwingproviders.CheckedProvider;

public interface MultiverseWorldProvider extends CheckedProvider<TerminableMultiverseWorld> {
    TerminableMultiverseWorld get() throws WorldCreationException;
}
