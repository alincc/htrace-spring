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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import no.nb.htrace.core.HTraceHttpHeaders;

public class TraceableRequest {
    private static final Random random = new SecureRandom();
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    private TraceScope traceScope;
    private String description;

    public TraceableRequest() {
        super();
        this.request = getRequestFromContext();
        this.response = getResponseFromContext();
    }

    public TraceableRequest(String description) {
        this();
        this.description = description;
    }

    public void startTrace() {
        if (isTracing()) {
            traceScope = Trace.startSpan(getDescription(), getCurrentSpan());
        } else {
            traceScope = Trace.startSpan(getDescription(), Sampler.ALWAYS);
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
            return Trace.startSpan(getDescription(), new TraceInfo(getTraceId(), getSpanId())).getSpan();
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

    public String getDescription() {
        return description;
    }

    private void annotateSpanWithResultCode(TraceScope traceScope) {
        traceScope.getSpan().addKVAnnotation("http.responsecode".getBytes(), (""+response.getStatus()).getBytes());
    }


    private void annotatateSpanWithRequestInfo(TraceScope traceScope) {
        traceScope.getSpan().addKVAnnotation("request".getBytes(), (request.getMethod() + " " + request.getRequestURI()).getBytes());
    }

}
