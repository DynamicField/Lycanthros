package com.github.jeuxjeux20.loupsgarous.config.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is just an annotation to stop IntelliJ from putting the deserializer constructors in grey.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.CONSTRUCTOR)
public @interface BukkitDeserialize {
}
