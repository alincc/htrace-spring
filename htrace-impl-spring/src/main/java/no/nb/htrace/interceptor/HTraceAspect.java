package no.nb.htrace.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.nb.htrace.annotations.Traceable;

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
            System.out.println("IS TRACING");
            System.out.println(Trace.currentSpan());
            traceScope = Trace.startSpan(description, Trace.currentSpan());
        } else  if (request.isTracing()) {
            TraceInfo parentSpan = request.getTraceInfo();
            System.out.println("CONTINUING TRACING");
            System.out.println(parentSpan);
            traceScope = Trace.startSpan(description, parentSpan);
        } else {
            System.out.println("STARTIG NEW SPAN");
            traceScope = Trace.startSpan(description);    
        }

        Object retVal = pjp.proceed();
        
        traceScope.getSpan().addKVAnnotation("request".getBytes(), (getRequestFromContext().getMethod() + " " + getRequestFromContext().getRequestURI()).getBytes());
        traceScope.getSpan().addKVAnnotation("http.responsecode".getBytes(), (""+getResponseFromContext().getStatus()).getBytes());

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
