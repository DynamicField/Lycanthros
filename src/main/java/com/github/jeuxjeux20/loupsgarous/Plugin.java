package com.github.jeuxjeux20.loupsgarous;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A binding annotation that gets something associated to the {@link LoupsGarous} plugin.
 */
@BindingAnnotation
@Retention(RUNTIME)
@Target({ TYPE, PARAMETER, FIELD })
public @interface Plugin {
}
