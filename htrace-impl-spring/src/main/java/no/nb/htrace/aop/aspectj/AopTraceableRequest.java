package no.nb.htrace.aop.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;

import no.nb.htrace.annotation.Traceable;

public class AopTraceableRequest extends TraceableRequest {
    private Traceable traceable;
    private ProceedingJoinPoint joinPoint;
    
    public AopTraceableRequest(ProceedingJoinPoint joinPoint, Traceable traceable) {
        super();
        this.joinPoint = joinPoint;
        this.traceable = traceable;
    }
    
    @Override
    public String getDescription() {
        String description = joinPoint.getSignature().getName();
        if (!traceable.description().isEmpty()) {
            description = traceable.description();
        }
        return description;
    }
    
    
}
