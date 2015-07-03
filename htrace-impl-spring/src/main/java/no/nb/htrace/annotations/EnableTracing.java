package no.nb.htrace.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import no.nb.htrace.interceptor.HTraceAspect;
import no.nb.htrace.interceptor.HTraceFeignInterceptor;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@EnableAspectJAutoProxy
@Import({no.nb.htrace.config.HTraceConfig.class, HTraceAspect.class, HTraceFeignInterceptor.class})
public @interface EnableTracing {

}
