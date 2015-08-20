package no.nb.htrace.aop.aspectj;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;

import no.nb.htrace.annotation.Traceable;
import no.nb.htrace.core.HTraceHttpHeaders;

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

    public boolean shouldTrace() {
        HttpServletRequest request = getRequestFromContext();
        String sampled = request.getHeader(HTraceHttpHeaders.SAMPLED.toString());
        return (sampled != null && "1".equals(sampled)) ?  true : false;
    }
    
    
}
