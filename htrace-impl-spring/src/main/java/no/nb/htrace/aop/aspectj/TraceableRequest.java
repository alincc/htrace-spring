package no.nb.htrace.aop.aspectj;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.htrace.Sampler;
import org.apache.htrace.Span;
import org.apache.htrace.Trace;
import org.apache.htrace.TraceScope;
import org.apache.htrace.impl.MilliSpan;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import no.nb.htrace.core.HTraceHttpHeaders;

public class TraceableRequest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private TraceScope traceScope;
    private String description;
    private String processId;

    public TraceableRequest() {
        super();
        this.request = getRequestFromContext();
        this.response = getResponseFromContext();
    }

    public TraceScope startTrace() {
        if (isTracing()) {
            traceScope = Trace.startSpan(getDescription(), getCurrentSpan());
        } else {
            traceScope = Trace.startSpan(getDescription(), Sampler.ALWAYS);
        }
        return traceScope;
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
            return new MilliSpan(getDescription(), getTraceId(), getSpanId(), getSpanId(), getProcessId());
        }
    }

    public boolean isTracing() {
        return Trace.isTracing() || request.getHeader(HTraceHttpHeaders.TRACE_ID.toString()) != null ? true : false;
    }

    private long getSpanId() {
        return Long.parseLong(request.getHeader(HTraceHttpHeaders.SPAN_ID.toString()));
    }

    private long getTraceId() {
        return Long.parseLong(request.getHeader(HTraceHttpHeaders.TRACE_ID.toString()));
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

    public String getProcessId() {
        if (processId  == null) {
            return "unknown";
        } else {
            return processId;
        }
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

}
