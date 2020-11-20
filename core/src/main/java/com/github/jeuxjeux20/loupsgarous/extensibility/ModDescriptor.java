package com.github.jeuxjeux20.loupsgarous.extensibility;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public final class ModDescriptor {
    private String name = "";
    private boolean hidden = false;
    private boolean enabledByDefault = false;
    private ItemStack item = new ItemStack(Material.BARRIER);

    public static ModDescriptor fromClass(Class<? extends Mod> describedClass) {
        ModDescriptor descriptor = new ModDescriptor();

        ModInfo annotation = describedClass.getAnnotation(ModInfo.class);
        if (annotation != null) {
            descriptor.setName(annotation.name());
            descriptor.setHidden(annotation.hidden());
            descriptor.setEnabledByDefault(annotation.enabledByDefault());

            try {
                ItemProvider itemProvider = annotation.item().getConstructor().newInstance();
                descriptor.setItem(itemProvider.get());
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "ItemProvider failed!", e);
            }
        }

        return descriptor;
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
