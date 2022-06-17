package io.github.zero88.jooqx.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jooq.Support;

/**
 * @since 2.0.0
 */
@Inherited
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLClientSupport {

    Support dialect();

    SQLClientType[] client();

}
