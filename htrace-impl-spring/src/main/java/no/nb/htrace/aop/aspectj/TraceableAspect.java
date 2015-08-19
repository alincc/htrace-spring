package no.nb.htrace.aop.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nb.htrace.annotation.Traceable;

@Aspect
@Component
public class TraceableAspect {

    @Value("${spring.application.name}") 
    private String processId;

    @Around("@annotation(traceable)")
    public Object process(ProceedingJoinPoint pjp, Traceable traceable) throws Throwable {
        AopTraceableRequest traceableRequest = new AopTraceableRequest(pjp, traceable);
        traceableRequest.setProcessId(processId);

        if (traceableRequest.shouldTrace()) {
            return traceAndProceed(pjp, traceableRequest);
        } else {
            return proceed(pjp);
        }
    }

    private Object traceAndProceed(ProceedingJoinPoint pjp,
            AopTraceableRequest traceableRequest) throws Throwable {
        traceableRequest.startTrace();
        try {
            return proceed(pjp);
        } finally {
            traceableRequest.endTrace();
        }
    }

    private Object proceed(ProceedingJoinPoint pjp) throws Throwable {
        return pjp.proceed();
    }

}
