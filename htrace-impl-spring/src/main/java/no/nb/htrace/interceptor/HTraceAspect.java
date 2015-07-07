package no.nb.htrace.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.nb.htrace.annotations.Traceable;

import org.apache.htrace.Sampler;
import org.apache.htrace.Trace;
import org.apache.htrace.TraceInfo;
import org.apache.htrace.TraceScope;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class HTraceAspect {

    @Around("@annotation(traceable)")
    public Object process(ProceedingJoinPoint pjp, Traceable traceable) throws Throwable {
        Request request = new Request(getRequestFromContext());
        
        TraceScope traceScope = null;
        
        String description = pjp.getSignature().getName();
        if (!traceable.description().isEmpty()) {
            description = traceable.description();
        }
        if (Trace.isTracing()) {
            traceScope = Trace.startSpan(description, Trace.currentSpan());
        } else  if (request.isTracing()) {
            TraceInfo parentSpan = request.getTraceInfo();
            traceScope = Trace.startSpan(description, parentSpan);
        } else {
            traceScope = Trace.startSpan(description, Sampler.ALWAYS);
        }

        Object retVal = pjp.proceed();
        
        try {
            traceScope.getSpan().addKVAnnotation("request".getBytes(), (getRequestFromContext().getMethod() + " " + getRequestFromContext().getRequestURI()).getBytes());
            traceScope.getSpan().addKVAnnotation("http.responsecode".getBytes(), (""+getResponseFromContext().getStatus()).getBytes());
        } catch(Exception ex) {
            //Nothing to do
        }

        traceScope.close();

        return retVal;
    }


    private HttpServletRequest getRequestFromContext() {
        ServletRequestAttributes t = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();  
        return t.getRequest();
    }

    private HttpServletResponse getResponseFromContext() {
        ServletRequestAttributes t = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();  
        return t.getResponse();
    }
}
