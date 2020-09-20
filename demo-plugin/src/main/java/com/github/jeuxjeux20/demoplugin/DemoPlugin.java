package com.github.jeuxjeux20.demoplugin;

import com.github.jeuxjeux20.loupsgarous.LoupsGarousRoot;
import org.bukkit.plugin.java.JavaPlugin;

public final class DemoPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        LoupsGarousRoot loupsGarous = LoupsGarousRoot.getCurrent();

        loupsGarous.getModRegistry().register(new StupidMod(), this);
        loupsGarous.getModRegistry().register(new PatateMod(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
