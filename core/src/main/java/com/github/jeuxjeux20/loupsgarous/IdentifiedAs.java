package com.github.jeuxjeux20.loupsgarous;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IdentifiedAs {
    String value();
}
