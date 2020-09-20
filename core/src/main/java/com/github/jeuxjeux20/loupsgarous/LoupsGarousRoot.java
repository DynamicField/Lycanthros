package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.loupsgarous.config.RootConfiguration;
import com.github.jeuxjeux20.loupsgarous.extensibility.ModDescriptorRegistry;
import com.github.jeuxjeux20.loupsgarous.extensibility.ModRegistry;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.util.NoSuchElementException;

public interface LoupsGarousRoot extends Plugin {
    LGGameManager getGameManager();

    ModRegistry getModRegistry();

    ModDescriptorRegistry getModDescriptorRegistry();

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
