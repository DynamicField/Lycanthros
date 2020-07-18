package com.github.jeuxjeux20.loupsgarous.game;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated elements are considered intrinsic. Intrinsic elements are
 * usually part of a two step (or more) pipeline, for example:
 * <pre>
 * +-------------------+
 * |     Endpoint      |
 * |     Factory       |
 * |                   |
 * |  +-------------+  |
 * |  |             |  |
 * |  |             |  |
 * |  |  Intrinsic  |  |
 * |  |   Factory   |  |
 * |  |             |  |
 * |  |             |  |
 * |  +------+------+  |
 * |         |         |
 * |         |         |
 * |  +------v------+  |
 * |  |             |  |
 * |  |             |  |
 * |  | Transformer |  |
 * |  |   Applier   |  |
 * |  |             |  |
 * |  |             |  |
 * |  +-------------+  |
 * |                   |
 * +-------------------+
 * </pre>
 */
@BindingAnnotation
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Intrinsic {
}
