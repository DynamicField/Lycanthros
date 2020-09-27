package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.relativesorting.OrderConstraints;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
    String[] after() default {};

    String[] before() default {};

    int position() default 0;

    final class Util {
        private Util() {
        }

        public static OrderConstraints toOrderConstraints(@Nullable Order order) {
            if (order == null) {
                return OrderConstraints.EMPTY;
            }

            return new OrderConstraints(
                    Arrays.asList(order.before()),
                    Arrays.asList(order.after()),
                    order.position()
            );
        }
    }
}
