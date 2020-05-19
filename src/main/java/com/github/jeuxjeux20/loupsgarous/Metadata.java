package com.github.jeuxjeux20.loupsgarous;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A qualifier to get metadata (most often, singleton) instances of objects.
 */
@Qualifier
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER, METHOD })
public @interface Metadata {
}
