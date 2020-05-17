package com.github.df.loupsgarous;

import com.github.df.loupsgarous.config.RootConfiguration;
import com.github.df.loupsgarous.extensibility.ModRegistry;
import com.github.df.loupsgarous.game.LGGameManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.util.NoSuchElementException;

public interface LoupsGarousRoot extends Plugin {
    LGGameManager getGameManager();

    ModRegistry getModRegistry();

    RootConfiguration.File getRootConfig();

    static LoupsGarousRoot getCurrent() {
        return getCurrent(Bukkit.getServer());
    }

    static LoupsGarousRoot getCurrent(Server server) {
        LoupsGarousRoot root = (LoupsGarousRoot) server.getPluginManager().getPlugin("LoupsGarous");

        if (root == null) {
            throw new NoSuchElementException("No LoupsGarous plugin found.");
        }

        return root;
    }
}
