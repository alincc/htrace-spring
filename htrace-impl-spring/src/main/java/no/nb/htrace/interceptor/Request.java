package no.nb.htrace.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.htrace.TraceInfo;

import no.nb.htrace.core.HTraceHttpHeaders;

public class Request {

    private HttpServletRequest request;

    public Request(HttpServletRequest request) {
        super();
        this.request = request;
    }
    
    public boolean isTracing() {
        return request.getHeader(HTraceHttpHeaders.TraceId.getName()) != null ? true : false;

    }

    public TraceInfo getTraceInfo() {
        return new TraceInfo(getTraceId(), getSpanId());
    }

    private long getSpanId() {
        return Long.parseLong(request.getHeader(HTraceHttpHeaders.SpanId.getName()));
    }

    private long getTraceId() {
        return Long.parseLong(request.getHeader(HTraceHttpHeaders.TraceId.getName()));
    }
    

}
