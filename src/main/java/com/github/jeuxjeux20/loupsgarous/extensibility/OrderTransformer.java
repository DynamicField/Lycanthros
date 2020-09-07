package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.relativesorting.OrderedElementTransformer;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface OrderTransformer {
    Class<? extends OrderedElementTransformer> value();
}
