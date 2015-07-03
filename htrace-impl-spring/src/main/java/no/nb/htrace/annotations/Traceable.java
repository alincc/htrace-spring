package no.nb.htrace.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME ) // the annotation will be available during runtime
@Target({ElementType.METHOD, ElementType.TYPE})         // this can just used in methods
public @interface Traceable {

    String description() default "";
}
