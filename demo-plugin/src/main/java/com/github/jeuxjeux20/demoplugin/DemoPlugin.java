package com.github.jeuxjeux20.demoplugin;

import com.github.jeuxjeux20.loupsgarous.LoupsGarousRoot;
import com.github.jeuxjeux20.loupsgarous.extensibility.ModEntry;
import org.bukkit.plugin.java.JavaPlugin;

public final class DemoPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        LoupsGarousRoot loupsGarous = LoupsGarousRoot.getCurrent();

        loupsGarous.getModRegistry().register(
                new ModEntry(this, "StupidMod", StupidMod::new),
                new ModEntry(this, "PatateMod", PatateMod::new)
        );
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
