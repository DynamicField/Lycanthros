package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.descriptor.Descriptor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ModDescriptor extends Descriptor<Mod> {
    private String name = "";
    private boolean hidden = false;
    private boolean enabledByDefault = false;
    private ItemStack item = new ItemStack(Material.BARRIER);

    public ModDescriptor(Class<? extends Mod> describedClass) {
        super(describedClass);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }

    public void setEnabledByDefault(boolean enabledByDefault) {
        this.enabledByDefault = enabledByDefault;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
