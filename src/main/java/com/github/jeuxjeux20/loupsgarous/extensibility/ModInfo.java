package com.github.jeuxjeux20.loupsgarous.extensibility;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ModInfo {
    String name() default "";

    boolean hidden() default false;

    boolean enabledByDefault() default false;

    Class<? extends ItemProvider> item() default DefaultItem.class;

    class DefaultItem implements ItemProvider {
        @Override
        public ItemStack get() {
            return new ItemStack(Material.NOTE_BLOCK);
        }
    }
}
