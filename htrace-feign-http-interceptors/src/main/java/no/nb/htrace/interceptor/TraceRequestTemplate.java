package no.nb.htrace.interceptor;

import org.apache.htrace.Span;

import feign.RequestTemplate;
import no.nb.htrace.core.HTraceHttpHeaders;

public class TraceRequestTemplate {
    private final RequestTemplate requestTemplate;
    
    public TraceRequestTemplate(RequestTemplate requestTemplate) {
        this.requestTemplate = requestTemplate;
    }

    public void addTraceHeadersToTemplate(Span span) {
        if (span != null) {
            requestTemplate.header(HTraceHttpHeaders.TRACE_ID, ""+span.getTraceId());
            requestTemplate.header(HTraceHttpHeaders.SPAN_ID, ""+span.getSpanId());
            requestTemplate.header(HTraceHttpHeaders.PARENT_SPAN_ID, ""+span.getParentId());
            requestTemplate.header(HTraceHttpHeaders.SAMPLED, "1");
            requestTemplate.header(HTraceHttpHeaders.FLAGS, "1");
        }
    }

}
