package no.nb.htrace.aop.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import no.nb.htrace.annotation.Traceable;

@Aspect
@Component
public class TraceableAspect {

    @Around("@annotation(traceable)")
    public Object process(ProceedingJoinPoint pjp, Traceable traceable) throws Throwable {
        TraceableRequest traceableRequest = new TraceableRequest(pjp, traceable);

        traceableRequest.startTrace();
        try {
            return pjp.proceed();
        } finally {
            traceableRequest.endTrace();
        }
    }

}
