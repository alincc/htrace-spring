package no.nb.htrace.aop.aspectj;

import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.htrace.Sampler;
import org.apache.htrace.Span;
import org.apache.htrace.Trace;
import org.apache.htrace.TraceInfo;
import org.apache.htrace.TraceScope;
import org.apache.htrace.impl.MilliSpan;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import no.nb.htrace.annotation.Traceable;
import no.nb.htrace.core.HTraceHttpHeaders;

public class TraceableRequest {
    private final static Random random = new SecureRandom();
    
    private Traceable traceable;
    private ProceedingJoinPoint joinPoint;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private TraceScope traceScope;

    public TraceableRequest(ProceedingJoinPoint joinPoint, Traceable traceable) {
        super();
        this.joinPoint = joinPoint;
        this.traceable = traceable;
        this.request = getRequestFromContext();
        this.response = getResponseFromContext();
    }

    public void startTrace() {
        if (isTracing()) {
            traceScope = Trace.startSpan(getDescriptionFromAnnotationOrMethodName(), getCurrentSpan());
        } else {
            traceScope = Trace.startSpan(getDescriptionFromAnnotationOrMethodName(), Sampler.ALWAYS);
        }
    }

    public void endTrace() {
        if (isTracing()) {
            annotatateSpanWithRequestInfo(traceScope);
            annotateSpanWithResultCode(traceScope);
        
            traceScope.close();
        }
    }

    private boolean isRequestBoundToThread() {
        return RequestContextHolder.getRequestAttributes() != null ? true : false;
    }
    
    private HttpServletRequest getRequestFromContext() {
        if (isRequestBoundToThread()) {
            ServletRequestAttributes t = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return t.getRequest();
        } 
        return new NullHttpServletRequest();
    }
    
    private HttpServletResponse getResponseFromContext() {
        if (isRequestBoundToThread()) {
            ServletRequestAttributes t = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();  
            return t.getResponse();
        }
        return new NullHttpServletResponse();
    }

    private Span getCurrentSpan() {
        if (Trace.isTracing()) {
            return Trace.currentSpan();
        } else {
            return new MilliSpan(getDescriptionFromAnnotationOrMethodName(), getTraceId(), getSpanId(),
                    random.nextLong(), "Unknown");
        }
    }

    public boolean isTracing() {
        return Trace.isTracing() || request.getHeader(HTraceHttpHeaders.TRACE_ID) != null ? true : false;
    }

    public TraceInfo getTraceInfo() {
        return new TraceInfo(getTraceId(), getSpanId());
    }

    private long getSpanId() {
        return Long.parseLong(request.getHeader(HTraceHttpHeaders.SPAN_ID));
    }

    private long getTraceId() {
        return Long.parseLong(request.getHeader(HTraceHttpHeaders.TRACE_ID));
    }

    public String getDescriptionFromAnnotationOrMethodName() {
        String description = joinPoint.getSignature().getName();
        if (!traceable.description().isEmpty()) {
            description = traceable.description();
        }
        return description;
    }

    private void annotateSpanWithResultCode(TraceScope traceScope) {
        traceScope.getSpan().addKVAnnotation("http.responsecode".getBytes(), (""+response.getStatus()).getBytes());
    }


    private void annotatateSpanWithRequestInfo(TraceScope traceScope) {
        traceScope.getSpan().addKVAnnotation("request".getBytes(), (request.getMethod() + " " + request.getRequestURI()).getBytes());
    }

}
